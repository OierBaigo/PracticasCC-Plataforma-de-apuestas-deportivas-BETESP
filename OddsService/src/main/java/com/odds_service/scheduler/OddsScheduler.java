package com.odds_service.scheduler;

import com.odds_service.service.OddsUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OddsScheduler {

    private static final Logger log = LoggerFactory.getLogger(OddsScheduler.class);

    private final OddsUpdateService oddsUpdateService;

    public OddsScheduler(OddsUpdateService oddsUpdateService) {
        this.oddsUpdateService = oddsUpdateService;
    }

    @Scheduled(fixedDelayString = "${odds.scheduler.fixed-delay-ms:300000}")
    public void refreshOdds() {
        try {
            log.info("Iniciando actualización automática de cuotas");
            oddsUpdateService.updateAllOdds();
            log.info("Actualización automática de cuotas completada");
        } catch (Exception exception) {
            log.error("Error durante la actualización de cuotas", exception);
        }
    }
}