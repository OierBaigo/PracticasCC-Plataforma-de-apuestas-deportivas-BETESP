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
}
