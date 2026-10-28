package com.match_service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.match_service.model.MatchSyncStatus;
import com.match_service.service.MatchService;

@Component
public class MatchScheduler {
	private static final Logger log = LoggerFactory.getLogger(MatchScheduler.class);
	
	private final MatchService matchService;
	final MatchSyncStatus matchSyncStatus;

    MatchScheduler(MatchService matchService, MatchSyncStatus matchSyncStatus) {
        this.matchService = matchService;
        this.matchSyncStatus = matchSyncStatus;
    }

    @Scheduled(fixedDelayString = "${match.scheduler.fixed-delay-ms:300000}")
    public void refreshMatch() {
        try {
            log.info("Iniciando actualización automática de partidos");
            matchService.syncLaLigaMatches(2026);
            matchSyncStatus.markInitialSyncCompleted();
            log.info("Actualización automática de partidos completada");
        } catch (Exception exception) {
            log.error("Error durante la actualización de partidos", exception);
        }
    }
}