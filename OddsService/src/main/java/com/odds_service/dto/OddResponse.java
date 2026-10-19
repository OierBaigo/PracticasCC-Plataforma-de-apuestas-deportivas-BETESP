package com.odds_service.dto;

import com.odds_service.model.ResultType;

public record OddResponse(Long id, Long matchId, ResultType resultType, Double value, boolean active) {}
