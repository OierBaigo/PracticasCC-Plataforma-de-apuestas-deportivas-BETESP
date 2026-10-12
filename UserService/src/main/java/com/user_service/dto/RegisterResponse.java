package com.user_service.dto;

public record RegisterResponse(UserResponse user, String accessToken, String tokenType) {}
