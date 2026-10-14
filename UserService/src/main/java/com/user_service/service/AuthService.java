package com.user_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired 
	private JwtService jwtService;
	
	public LoginResponse login(LoginRequest request) {
		
		User user = userRepo.findByEmail(request.email());
		
		if(user == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new InvalidCredentialsException("El correo o la contraseña son incorrectos");
		}
		
		String token = jwtService.generateToken(user);
		
		return new LoginResponse(token, "Bearer");
	}
}
