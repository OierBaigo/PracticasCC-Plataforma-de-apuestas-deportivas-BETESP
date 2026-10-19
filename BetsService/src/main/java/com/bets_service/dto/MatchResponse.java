package com.bets_service.dto;

import java.time.LocalDateTime;

public record MatchResponse(Long id, String homeTeam, String awayTeam, 
		LocalDateTime dateTime, String competition, Integer matchDay, String status, Integer homeScore, Integer awayScore) {}