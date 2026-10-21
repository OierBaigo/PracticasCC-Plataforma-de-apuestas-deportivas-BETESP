package com.settlement_service.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateBetStatusRequest(@NotNull String status) {}
