package com.odds_service.controller;

import com.odds_service.service.TeamRankingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/rankings")
public class TeamRankingController {

	private final TeamRankingService rankingService;

    TeamRankingController(TeamRankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeRankings() {
        int total = rankingService.insertInitialRankings();
        return ResponseEntity.ok(Map.of("message", "Rankings inicializados correctamente","teamsProcessed", total));
    }
}