package com.bets_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;

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
	
	private final BetRepo betRepository;
	private final MatchServiceClient matchServiceClient;
	private final OddsServiceClient oddsServiceClient;
	private final UserServiceClient userServiceClient;

    BetsService(BetRepo betRepository, MatchServiceClient matchServiceClient, OddsServiceClient oddsServiceClient, UserServiceClient userServiceClient) {
        this.betRepository = betRepository;
        this.matchServiceClient = matchServiceClient;
        this.oddsServiceClient = oddsServiceClient;
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    public BetResponse createBet(Long userId, CreateBetRequest request) {
        if (isStakePositive(request)) {
            throw new IllegalArgumentException("El importe de la apuesta debe ser positivo");
        }
        
        MatchResponse match;
        try {
        	match = matchServiceClient.getMatch(request.matchId());
        } catch(RestClientResponseException ex) {
        	throw ex;
        }
        
        
        if (match == null || matchStatusIsNotFinishedOrTimed(match)) {
            throw new IllegalArgumentException("El partido no está disponible para apostar");
        }
        
        Double odds;
        try {
        	odds = oddsServiceClient.getActiveOdd(request.matchId(), request.resultType());
        } catch(RestClientResponseException ex) {
        	throw ex;
        }
                
        if (isOddInvalid(odds)) {
            throw new IllegalStateException("La cuota no es válida");
        }
        
        Double balance;
        try {
        	balance = userServiceClient.getBalance();
        } catch(RestClientResponseException ex) {
        	throw ex;
        }
        
        if (isBalanceInvalid(balance)) {
            throw new IllegalStateException("El saldo no es válido");
        }

        if (isBalanceLessThanAmount(request, balance)) {
            throw new IllegalArgumentException( "No tienes saldo suficiente para realizar la apuesta");
        }

        double potentialReturn = BigDecimal.valueOf(request.stake())
                .multiply(BigDecimal.valueOf(odds))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        try {
        	userServiceClient.withdrawBalance(request.stake());
        } catch(RestClientResponseException ex) {
        	throw ex;
        }
        
        Bet bet = new Bet();
        bet.setUserId(userId);
        bet.setMatchId(request.matchId());
        bet.setResultType(request.resultType());
        bet.setStake(request.stake());
        bet.setOddsAtPlacement(odds);
        bet.setPotentialReturn(potentialReturn);
        bet.setStatus(BetStatus.PENDING);

        Bet savedBet = betRepository.saveAndFlush(bet);
                
        return toResponse(savedBet);
    }

	private boolean isBalanceLessThanAmount(CreateBetRequest request, Double balance) {
		return balance < request.stake();
	}

	private boolean isBalanceInvalid(Double balance) {
		return balance == null || !Double.isFinite(balance);
	}

	private boolean isOddInvalid(Double odds) {
		return isBalanceInvalid(odds) || odds <= 1.0;
	}

	private boolean matchStatusIsNotFinishedOrTimed(MatchResponse match) {
		return !"SCHEDULED".equalsIgnoreCase(match.status()) && !"TIMED".equalsIgnoreCase(match.status());
	}

	private boolean isStakePositive(CreateBetRequest request) {
		return request.stake() == null || !Double.isFinite(request.stake()) || request.stake() <= 0;
	}

    private BetResponse toResponse(Bet bet) {
        return new BetResponse(
                bet.getId(),
                bet.getUserId(),
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
	
	@Transactional
	public void updateBetStatus(Long betId, BetStatus status) {
	    Bet bet = betRepository.findById(betId).orElseThrow(() -> new IllegalArgumentException("No existe la apuesta con ID: " + betId));

	    if (isBalanceStatusNotPending(bet)) {
	        throw new IllegalStateException("La apuesta ya ha sido procesada: " + betId);
	    }

	    bet.setStatus(status);
	    betRepository.save(bet);
	}

	private boolean isBalanceStatusNotPending(Bet bet) {
		return bet.getStatus() != BetStatus.PENDING;
	}
}