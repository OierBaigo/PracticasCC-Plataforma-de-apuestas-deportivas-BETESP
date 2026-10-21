package com.settlement_service.dto;

import java.time.LocalDateTime;

public record BetResponse(
	    Long id,
	    Long userId,
	    Long matchId,
	    String resultType,
	    Double stake,
	    Double oddsAtPlacement,
	    Double potentialReturn,
	    String status,
	    LocalDateTime createdAt
	) {}
