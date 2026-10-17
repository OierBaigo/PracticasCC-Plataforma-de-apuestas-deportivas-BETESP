package com.odds_service.repository;

import com.odds_service.model.TeamRanking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRankingRepo extends JpaRepository<TeamRanking, Long> {
    Optional<TeamRanking> findByTeamNameIgnoreCase(String teamName);
}