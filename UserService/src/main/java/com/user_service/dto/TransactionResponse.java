package com.user_service.dto;

import java.time.LocalDateTime;

import com.user_service.model.TransactionType;

public record TransactionResponse(Long id, TransactionType operation, Double amount, Double finalBlance, LocalDateTime createdAt) {}
