package com.user_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_service.dto.LoginRequest;
import com.user_service.dto.LoginResponse;
import com.user_service.exception.InvalidCredentialsException;
import com.user_service.model.User;
import com.user_service.repository.UserRepo;
import com.user_service.security.JwtService;

@Service
public class AuthService {
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}
	
	public LoginResponse login(LoginRequest request) {
		
		User user = userRepo.findByEmail(request.email());
		
		if(user == null || incorrectPassword(request, user)) {
			throw new InvalidCredentialsException("El correo o la contraseña son incorrectos");
		}
		
		String token = jwtService.generateToken(user);
		
		return new LoginResponse(token, "Bearer");
	}

	private boolean incorrectPassword(LoginRequest request, User user) {
		return !passwordEncoder.matches(request.password(), user.getPassword());
	}
}
