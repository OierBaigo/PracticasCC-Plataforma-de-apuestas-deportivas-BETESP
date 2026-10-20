package com.settlement_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {

    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    public void handleFinishedMatch(Long matchId) {
        log.info("Recibida notificación de partido finalizado: {}", matchId);
        System.out.println("Recibida notificación de partido finalizado: " + matchId);
    }
}