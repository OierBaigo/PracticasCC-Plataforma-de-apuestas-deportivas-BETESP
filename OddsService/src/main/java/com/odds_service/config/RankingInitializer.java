package com.odds_service.config;

import com.odds_service.service.TeamRankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RankingInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RankingInitializer.class);

    private final TeamRankingService rankingService;

    public RankingInitializer(TeamRankingService rankingService) {
        this.rankingService = rankingService;
    }

    @Override
    public void run(ApplicationArguments args) {
        int total = rankingService.insertInitialRankings();
        log.info("Rankings inicializados correctamente. Equipos procesados: {}", total);
    }
}
