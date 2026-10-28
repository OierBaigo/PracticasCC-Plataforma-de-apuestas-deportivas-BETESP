package com.bets_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.bets_service.dto.BetResponse;
import com.bets_service.dto.CreateBetRequest;
import com.bets_service.dto.UpdateBetStatusRequest;
import com.bets_service.service.BetsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bets")
public class BetController {

	private final BetsService bettingService;

    BetController(BetsService bettingService) {
        this.bettingService = bettingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BetResponse createBet( @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateBetRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return bettingService.createBet(userId, request);
    }
    

	@GetMapping("/internal/match/{matchId}")
	public List<BetResponse> getPendingBetsByMatch(@PathVariable Long matchId) {
	    return bettingService.getPendingBetsByMatch(matchId);
	}
	
	@PostMapping("/internal/{betId}/status")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateBetStatus(@PathVariable Long betId, @RequestBody UpdateBetStatusRequest request) {
	    bettingService.updateBetStatus(betId, request.status());
	}
}