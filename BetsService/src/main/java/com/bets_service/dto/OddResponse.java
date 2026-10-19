package com.bets_service.dto;


public record OddResponse(Long id, Long matchId, String resultType, Double value, boolean active) {}

