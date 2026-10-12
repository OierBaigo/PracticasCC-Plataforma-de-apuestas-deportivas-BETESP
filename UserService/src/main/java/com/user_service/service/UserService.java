package com.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.dto.AmountRequest;
import com.user_service.dto.BalanceResponse;
import com.user_service.dto.RegisterRequest;
import com.user_service.dto.RegisterResponse;
import com.user_service.dto.UserResponse;
import com.user_service.exception.EmailAlreadyExistsException;
import com.user_service.exception.InsufficientBalanceException;
import com.user_service.exception.UserNotExistsException;
import com.user_service.model.User;
import com.user_service.repository.UserRepo;
import com.user_service.security.JwtService;

@Service
public class UserService {
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired 
	private JwtService jwtService;
	
	public RegisterResponse registerUser(RegisterRequest request) {
		
		if(userRepo.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("El email ya existe");
		}
		
		User user = new User();
		user.setEmail(request.email());
		user.setUsername(request.username());
		user.setPassword(passwordEncoder.encode(request.password()));
		
		User savedUser = userRepo.save(user);
		
		String token = jwtService.generateToken(savedUser.getId());
		
		UserResponse userResponse = new UserResponse(savedUser.getId(),savedUser.getEmail(), savedUser.getUsername(), savedUser.getBalance());
		
		return new RegisterResponse(userResponse, token, "Bearer");
	}
	
	public UserResponse getUserById(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() ->
		            new UserNotExistsException("Usuario no encontrado"));

		return new UserResponse(user.getId(),user.getEmail(), user.getUsername(), user.getBalance());
	}
	
	public BalanceResponse getUserBalance(Long userId) {
		User user = userRepo.findById(userId).orElseThrow(() ->
		            new UserNotExistsException("Usuario no encontrado"));

		return new BalanceResponse(user.getBalance());
	}
	
	@Transactional
	public BalanceResponse deposit(Long userId, Double amount) {
		User user = userRepo.findById(userId).orElseThrow(() ->
		            new UserNotExistsException("Usuario no encontrado"));

		user.setBalance(user.getBalance() + amount);
		
		userRepo.save(user);
		
		return new BalanceResponse(user.getBalance());
	}
	
	@Transactional
	public BalanceResponse withdraw(Long userId, Double amount) {
		User user = userRepo.findById(userId).orElseThrow(() ->
		            new UserNotExistsException("Usuario no encontrado"));
		
		if (user.getBalance() < amount) {
	        throw new InsufficientBalanceException("No tienes saldo suficiente para realizar esta operación");
	    }
		
		user.setBalance(user.getBalance() - amount);
		
		userRepo.save(user);
		
		return new BalanceResponse(user.getBalance());
	}
}
