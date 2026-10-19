package com.bets_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record WithdrawRequest(
		@NotNull(message = "La cantidad es obligatoria")
	    @DecimalMin(value = "0.0", inclusive = false,
	                message = "La cantidad debe ser mayor que cero")
	    Double amount
	    ) {}
