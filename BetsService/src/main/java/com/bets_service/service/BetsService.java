package com.bets_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bets_service.dto.MatchResponse;
import com.bets_service.client.MatchServiceClient;
import com.bets_service.client.OddsServiceClient;
import com.bets_service.client.UserServiceClient;
import com.bets_service.dto.BetResponse;
import com.bets_service.dto.CreateBetRequest;
import com.bets_service.model.Bet;
import com.bets_service.model.BetStatus;
import com.bets_service.repository.BetRepo;

@Service
public class BetsService {
	
	@Autowired
    private BetRepo betRepository;
	@Autowired
    private MatchServiceClient matchServiceClient;
	@Autowired
    private OddsServiceClient oddsServiceClient;
	@Autowired
    private UserServiceClient userServiceClient;

    @Transactional
    public BetResponse createBet(Long userId, CreateBetRequest request) {
    	 System.out.println("BET 1: Inicio de createBet");

        if (request.stake() == null || !Double.isFinite(request.stake()) || request.stake() <= 0) {
            throw new IllegalArgumentException("El importe de la apuesta debe ser positivo");
        }

        MatchResponse match = matchServiceClient.getMatch(request.matchId());

        System.out.println("BET 2: Partido recibido: " + match);
        
        if (match == null || !"SCHEDULED".equalsIgnoreCase(match.status())) {
            throw new IllegalArgumentException("El partido no está disponible para apostar");
        }
        
        System.out.println("BET 3: Partido válido");
        
        Double odds = oddsServiceClient.getActiveOdd(request.matchId(), request.resultType());
        
        System.out.println("BET 4: Cuota recibida: " + odds);
        
        if (odds == null || !Double.isFinite(odds) || odds <= 1.0) {
            throw new IllegalStateException("La cuota no es válida");
        }

        Double balance = userServiceClient.getBalance();

        System.out.println("BET 5: Saldo recibido: " + balance);
        
        if (balance == null || !Double.isFinite(balance)) {
            throw new IllegalStateException("El saldo no es válido");
        }

        if (balance < request.stake()) {
            throw new IllegalArgumentException( "No tienes saldo suficiente para realizar la apuesta");
        }

        double potentialReturn = BigDecimal.valueOf(request.stake())
                .multiply(BigDecimal.valueOf(odds))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        userServiceClient.withdrawBalance(request.stake());
        
        System.out.println("BET 6: Saldo descontado");

        Bet bet = new Bet();
        bet.setUserId(userId);
        bet.setMatchId(request.matchId());
        bet.setResultType(request.resultType());
        bet.setStake(request.stake());
        bet.setOddsAtPlacement(odds);
        bet.setPotentialReturn(potentialReturn);
        bet.setStatus(BetStatus.PENDING);

        Bet savedBet = betRepository.saveAndFlush(bet);
        
        System.out.println("BET 7: Guardado apuesta");
        
        return toResponse(savedBet);
    }

    private BetResponse toResponse(Bet bet) {
        return new BetResponse(
                bet.getId(),
                bet.getMatchId(),
                bet.getResultType(),
                bet.getStake(),
                bet.getOddsAtPlacement(),
                bet.getPotentialReturn(),
                bet.getStatus(),
                bet.getCreatedAt()
        );
    }
    

	public List<BetResponse> getPendingBetsByMatch(Long matchId) {
	    return betRepository.findByMatchIdAndStatus(matchId, BetStatus.PENDING).stream()
	    		.map(bet -> toResponse(bet))
	    		.toList();
	}
}