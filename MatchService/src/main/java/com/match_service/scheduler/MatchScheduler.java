package com.match_service.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.match_service.service.MatchService;

@Component
public class MatchScheduler {
	private static final Logger log = LoggerFactory.getLogger(MatchScheduler.class);
	
	@Autowired
    private MatchService matchService;

    @Scheduled(fixedDelayString = "${match.scheduler.fixed-delay-ms:300000}")
    public void refreshMatch() {
        try {
            log.info("Iniciando actualización automática de partidos");
            matchService.syncLaLigaMatches(2026);
            log.info("Actualización automática de partidos completada");
        } catch (Exception exception) {
            log.error("Error durante la actualización de partidos", exception);
        }
    }
}