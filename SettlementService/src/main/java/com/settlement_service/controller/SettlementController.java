package com.settlement_service.controller;

import com.settlement_service.dto.MatchResponse;
import com.settlement_service.service.SettlementService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/settlement")
public class SettlementController {

	@Autowired
    private SettlementService settlementService;
	
	private static final Logger log = LoggerFactory.getLogger(SettlementController.class);

    @PostMapping("/matches/{matchId}/finished")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void notifyMatchFinished(@PathVariable Long matchId, @RequestBody MatchResponse match) {
    	log.info("LIQUIDACION 3: Petición recibida. matchId={}, estado={}, marcador={}-{}",
    	        matchId,
    	        match.status(),
    	        match.homeScore(),
    	        match.awayScore());
        settlementService.handleFinishedMatch(matchId, match);
        log.info("LIQUIDACION 4: handleFinishedMatch terminó correctamente para partido {}",
                matchId);
    }
}