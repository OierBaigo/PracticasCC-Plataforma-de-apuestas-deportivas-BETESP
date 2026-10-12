package com.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.AmountRequest;
import com.user_service.dto.BalanceResponse;
import com.user_service.dto.RegisterRequest;
import com.user_service.dto.RegisterResponse;
import com.user_service.dto.UserResponse;
import com.user_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/register")
	@ResponseStatus(HttpStatus.CREATED)
	public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
		return userService.registerUser(request);
	}
	
	@GetMapping(value = "/me")
	@ResponseStatus(HttpStatus.OK)
	public UserResponse getMe(@AuthenticationPrincipal Jwt jwt) {
		Long userId = Long.valueOf(jwt.getSubject());
		return userService.getUserById(userId);
	}
	
	@GetMapping(value = "/me/balance")
	@ResponseStatus(HttpStatus.OK)
	public BalanceResponse getMyBalance(@AuthenticationPrincipal Jwt jwt) {
		Long userId = Long.valueOf(jwt.getSubject());
		return userService.getUserBalance(userId);
	}
	
	@PostMapping(value = "/me/deposit")
	@ResponseStatus(HttpStatus.OK)
	public BalanceResponse deposit(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AmountRequest request) {
		Long userId = Long.valueOf(jwt.getSubject());
		return userService.deposit(userId, request.amount());
	}
	
	@PostMapping(value = "/me/withdraw")
	@ResponseStatus(HttpStatus.OK)
	public BalanceResponse withdraw(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody AmountRequest request) {
		Long userId = Long.valueOf(jwt.getSubject());
		return userService.withdraw(userId, request.amount());
	}
}
