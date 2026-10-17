package com.odds_service.repository;

import com.odds_service.model.Odd;
import com.odds_service.model.ResultType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface OddRepo extends JpaRepository<Odd,Long> {
	List<Odd> findByMatchId(Long matchId);

    List<Odd> findByMatchIdAndActiveTrue(Long matchId);

    Optional<Odd> findByMatchIdAndResultType(Long matchId, ResultType resultType);
}
