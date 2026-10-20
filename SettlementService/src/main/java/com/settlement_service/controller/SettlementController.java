package com.settlement_service.controller;

import com.settlement_service.service.SettlementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/settlement")
public class SettlementController {

	@Autowired
    private SettlementService settlementService;

    @PostMapping("/matches/{matchId}/finished")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void notifyMatchFinished(@PathVariable Long matchId) {
        settlementService.handleFinishedMatch(matchId);
    }
}