package com.bets_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bets_service.model.Bet;
import com.bets_service.model.BetStatus;

public interface BetRepo extends JpaRepository<Bet, Long> {

    List<Bet> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Bet> findByMatchId(Long matchId);

	List<Bet> findByMatchIdAndStatus(Long matchId, BetStatus status);

}
