package com.odds_service.service;

import com.odds_service.model.TeamRanking;
import com.odds_service.repository.TeamRankingRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TeamRankingService {

	private final TeamRankingRepo rankingRepo;

    TeamRankingService(TeamRankingRepo rankingRepo) {
        this.rankingRepo = rankingRepo;
    }

    @Transactional
    public int insertInitialRankings() {
        Map<String, Integer> rankings = new LinkedHashMap<>();

        rankings.put("FC Barcelona", 27); 
        rankings.put("Club Atlético de Madrid", 22); 
        rankings.put("Real Madrid CF", 21); 
        rankings.put("Real Betis Balompié", 19); 
        rankings.put("Real Sociedad", 16); 
        rankings.put("Sevilla FC", 14);
        rankings.put("Deportivo Alavés", 11); 
        rankings.put("RC Celta de Vigo", 11); 
        rankings.put("RC Deportivo La Coruña", 11); 
        rankings.put("CA Osasuna", 11);
        rankings.put("Athletic Club", 9); 
        rankings.put("Villarreal CF", 9); 
        rankings.put("Rayo Vallecano de Madrid", 9); 
        rankings.put("Getafe CF", 9); 
        rankings.put("RCD Espanyol de Barcelona", 8); 
        rankings.put("Real Racing Club de Santander", 8); 
        rankings.put("Levante UD", 7); 
        rankings.put("Elche CF", 7); 
        rankings.put("Valencia CF", 6); 
        rankings.put("Málaga CF", 4); 

        for (Map.Entry<String, Integer> entry : rankings.entrySet()) {
            TeamRanking ranking = rankingRepo.findByTeamNameIgnoreCase(entry.getKey()).orElseGet(TeamRanking::new);

            ranking.setTeamName(entry.getKey());
            ranking.setRatingPoints(entry.getValue());

            rankingRepo.save(ranking);
        }

        return rankings.size();
    }
}