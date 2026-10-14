package com.match_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.match_service.model.Match;
import com.match_service.repository.MatchRepo;

@Service
public class MatchService {
	@Autowired
    private MatchRepo matchRepository;

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElse(null);
    }

    public Match saveMatch(Match match) {
        return matchRepository.save(match);
    }
    
    public List<Match> getMatchesByCompetition(String competition) {
        return matchRepository.findByCompetition(competition);
    }
}