package com.match_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.match_service.model.Match;


public interface MatchRepo extends JpaRepository<Match,Long>{
	List<Match> findByCompetition(String competition);
}
