package com.bets_service.dto;

import com.bets_service.model.BetStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateBetStatusRequest(@NotNull BetStatus status) {}
