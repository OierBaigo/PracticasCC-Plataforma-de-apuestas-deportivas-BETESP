package com.match_service.dto;

import java.time.LocalDateTime;

import com.match_service.model.StatusType;

public record MatchResponse(Long id, String homeTeam, String awayTeam, 
		LocalDateTime dateTime, String competition, Integer matchDay, StatusType status, Integer homeScore, Integer awayScore) {}
