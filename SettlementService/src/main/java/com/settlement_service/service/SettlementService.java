package com.settlement_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

import com.settlement_service.client.BetServiceClient;
import com.settlement_service.client.UserServiceClient;
import com.settlement_service.dto.BetResponse;
import com.settlement_service.dto.MatchResponse;
import com.settlement_service.model.ResultType;

@Service
public class SettlementService {
	@Autowired
	private BetServiceClient betServiceClient;
	@Autowired
	private UserServiceClient userServiceClient;

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    public void handleFinishedMatch(Long matchId, MatchResponse match) {
        log.info("Recibida notificación de partido finalizado: {}", matchId);
        System.out.println("Recibida notificación de partido finalizado: " + matchId);
        
        if (!matchId.equals(match.id())) {
            throw new IllegalArgumentException("El ID de la ruta no coincide con el ID del partido");
        }

        if (!"FINISHED".equals(match.status())) {
            throw new IllegalArgumentException("El partido todavía no ha finalizado");
        }

        if (match.homeScore() == null || match.awayScore() == null) {
            throw new IllegalArgumentException("El partido no tiene un resultado completo");
        }
        
        log.info("LIQUIDACION 6: Resultado validado para partido {}. Marcador {}-{}",
                matchId, match.homeScore(), match.awayScore());
        
        ResultType result;

        if (match.homeScore() > match.awayScore()) {
            result = ResultType.HOME_WIN;
        } else if (match.homeScore() < match.awayScore()) {
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
        	if(ResultType.valueOf(bet.resultType()) == result) {
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
}