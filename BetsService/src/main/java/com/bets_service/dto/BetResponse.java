package com.bets_service.dto;

import java.time.LocalDateTime;

import com.bets_service.model.BetStatus;
import com.bets_service.model.ResultType;

public record BetResponse(
    Long id,
    Long userId,
    Long matchId,
    ResultType resultType,
    Double stake,
    Double oddsAtPlacement,
    Double potentialReturn,
    BetStatus status,
    LocalDateTime createdAt
) {}
