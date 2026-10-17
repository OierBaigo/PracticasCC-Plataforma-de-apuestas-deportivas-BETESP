package com.odds_service.dto;

public record MatchResponse(Long id, String homeTeam, String awayTeam, String status) {}