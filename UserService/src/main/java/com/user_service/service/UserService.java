package com.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_service.dto.RegisterRequest;
import com.user_service.dto.UserResponse;
import com.user_service.exception.EmailAlreadyExistsException;
import com.user_service.model.User;
import com.user_service.repository.UserRepo;

@Service
public class UserService {
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public UserResponse registerUser(RegisterRequest request) {
		
		if(userRepo.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("El email ya existe");
		}
		
		User user = new User();
		user.setEmail(request.email());
		user.setUsername(request.username());
		user.setPassword(passwordEncoder.encode(request.password()));
		
		User savedUser = userRepo.save(user);
		
		return new UserResponse(savedUser.getId(),savedUser.getEmail(), savedUser.getUsername(), savedUser.getBalance());
	}
}
