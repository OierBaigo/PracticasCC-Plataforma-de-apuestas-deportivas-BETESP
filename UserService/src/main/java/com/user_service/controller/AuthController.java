package com.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.LoginRequest;
import com.user_service.dto.LoginResponse;
import com.user_service.service.AuthService;

import jakarta.validation.Valid;

@RestController
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@PostMapping(value = "/auth/login")
	@ResponseStatus(HttpStatus.OK)
	public LoginResponse register(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}
}
