package com.bets_service.dto;

import com.bets_service.model.ResultType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateBetRequest(
	    @NotNull @Positive Long matchId,
	    @NotNull ResultType resultType,
	    @NotNull @Positive Double stake
	) {}
