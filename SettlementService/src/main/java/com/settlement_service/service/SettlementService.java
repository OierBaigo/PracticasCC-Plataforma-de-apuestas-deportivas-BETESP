package com.settlement_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import com.settlement_service.client.BetServiceClient;
import com.settlement_service.client.UserServiceClient;
import com.settlement_service.dto.BetResponse;
import com.settlement_service.dto.MatchResponse;
import com.settlement_service.model.ResultType;

@Service
public class SettlementService {
	private final BetServiceClient betServiceClient;
	private final UserServiceClient userServiceClient;

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    SettlementService(BetServiceClient betServiceClient, UserServiceClient userServiceClient) {
        this.betServiceClient = betServiceClient;
        this.userServiceClient = userServiceClient;
    }

    public void handleFinishedMatch(Long matchId, MatchResponse match) {
        log.info("Recibida notificación de partido finalizado: {}", matchId);
        System.out.println("Recibida notificación de partido finalizado: " + matchId);
        
        if (isDifferentMatch(matchId, match)) {
            throw new IllegalArgumentException("El ID de la ruta no coincide con el ID del partido");
        }

        if (matchNotFinished(match)) {
            throw new IllegalArgumentException("El partido todavía no ha finalizado");
        }

        if (matchWithoutResult(match)) {
            throw new IllegalArgumentException("El partido no tiene un resultado completo");
        }
        
        log.info("LIQUIDACION 6: Resultado validado para partido {}. Marcador {}-{}",
                matchId, match.homeScore(), match.awayScore());
        
        ResultType result;

        if (isHomeWin(match)) {
            result = ResultType.HOME_WIN;
        } else if (isAwayWin(match)) {
            result = ResultType.AWAY_WIN;
        } else {
            result = ResultType.DRAW;
        }
        
        log.info("LIQUIDACION 7: Resultado ganador calculado: {}", result);
        
        List<BetResponse> bets;
        try {
        	bets = betServiceClient.getPendingBetsByMatch(matchId);
        } catch(RestClientResponseException ex) {
        	throw ex;
        }
        
        log.info("LIQUIDACION 8: Betting Service devolvió {} apuestas pendientes para partido {}",
                bets.size(), matchId);
        
        for(BetResponse bet: bets) {
        	if(isWinnedBet(result, bet)) {
        		try {
        			log.info("LIQUIDACION 10: Depositando {} al usuario {} por apuesta {}",
        	                bet.potentialReturn(), bet.userId(), bet.id());
        			
        			userServiceClient.depositWiningBet(bet.userId(), bet.potentialReturn());
        			log.info("LIQUIDACION 11: Depósito completado para apuesta {}",
                            bet.id());
            		betServiceClient.updateBetStatus(bet.id(), "WON");
            		log.info("LIQUIDACION 12: Estado {} enviado para apuesta {}",
                            "WON", bet.id());
        		} catch(RestClientResponseException ex) {
                	throw ex;
                }
        	} else {
        		try {
        			betServiceClient.updateBetStatus(bet.id(), "LOST");
        			log.info("LIQUIDACION 12: Estado {} enviado para apuesta {}",
                            "LOST", bet.id());
        		} catch(RestClientResponseException ex) {
                	throw ex;
                }
        	}
        }
    }

	private boolean isWinnedBet(ResultType result, BetResponse bet) {
		return ResultType.valueOf(bet.resultType()) == result;
	}

	private boolean isAwayWin(MatchResponse match) {
		return match.homeScore() < match.awayScore();
	}

	private boolean isHomeWin(MatchResponse match) {
		return match.homeScore() > match.awayScore();
	}

	private boolean matchWithoutResult(MatchResponse match) {
		return match.homeScore() == null || match.awayScore() == null;
	}

	private boolean matchNotFinished(MatchResponse match) {
		return !"FINISHED".equals(match.status());
	}

	private boolean isDifferentMatch(Long matchId, MatchResponse match) {
		return !matchId.equals(match.id());
	}
}