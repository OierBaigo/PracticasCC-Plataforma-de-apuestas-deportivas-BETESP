package com.bets_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.bets_service.dto.BetResponse;
import com.bets_service.dto.CreateBetRequest;
import com.bets_service.service.BetsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/bets")
public class BetController {

	@Autowired
    private BetsService bettingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BetResponse createBet( @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateBetRequest request) {
        Long userId = Long.valueOf(jwt.getSubject());
        return bettingService.createBet(userId, request);
    }
}