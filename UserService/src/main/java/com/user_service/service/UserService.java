package com.user_service.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.dto.BalanceResponse;
import com.user_service.dto.RegisterRequest;
import com.user_service.dto.RegisterResponse;
import com.user_service.dto.TransactionResponse;
import com.user_service.dto.UserResponse;
import com.user_service.exception.EmailAlreadyExistsException;
import com.user_service.exception.InsufficientBalanceException;
import com.user_service.exception.UserNotExistsException;
import com.user_service.model.Transaction;
import com.user_service.model.TransactionType;
import com.user_service.model.User;
import com.user_service.repository.TransactionRepo;
import com.user_service.repository.UserRepo;
import com.user_service.security.JwtService;

@Service
public class UserService {
	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final TransactionRepo transactionRepo;

	UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, TransactionRepo transactionRepo) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.transactionRepo = transactionRepo;
	}
	
	public RegisterResponse registerUser(RegisterRequest request) {
		
		if(userRepo.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException("El email ya existe");
		}
		
		User user = new User();
		user.setEmail(request.email());
		user.setUsername(request.username());
		user.setPassword(passwordEncoder.encode(request.password()));
		
		User savedUser = userRepo.save(user);
		
		String token = jwtService.generateToken(savedUser);
		
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
		User user = userRepo.findByIdForUpdate(userId).orElseThrow(() ->
		            new UserNotExistsException("Usuario no encontrado"));

		Transaction transaction = new Transaction(user, TransactionType.DEPOSIT, amount, user.getBalance() + amount);
		
		user.setBalance(user.getBalance() + amount);
		
		userRepo.save(user);
		transactionRepo.save(transaction);
		
		return new BalanceResponse(user.getBalance());
	}
	
	@Transactional
	public BalanceResponse withdraw(Long userId, Double amount) {
		User user = userRepo.findByIdForUpdate(userId).orElseThrow(() ->
		            new UserNotExistsException("Usuario no encontrado"));
		
		if (isBalanceLessThanAmount(amount, user)) {
	        throw new InsufficientBalanceException("No tienes saldo suficiente para realizar esta operación");
	    }
		
		Transaction transaction = new Transaction(user, TransactionType.WITHDRAWAL, amount, user.getBalance() - amount);
				
		user.setBalance(user.getBalance() - amount);
		
		userRepo.save(user);
		transactionRepo.save(transaction);
		
		return new BalanceResponse(user.getBalance());
	}

	private boolean isBalanceLessThanAmount(Double amount, User user) {
		return user.getBalance() < amount;
	}
	
	public List<TransactionResponse> getTransactions(Long userId) {
		userRepo.findById(userId).orElseThrow(() ->
        			new UserNotExistsException("Usuario no encontrado"));
		
		return transactionRepo.findByUserIdOrderByCreatedAtDesc(userId).stream().
				map(transaction -> new TransactionResponse(
						transaction.getId(),
						transaction.getOperation(),
						transaction.getAmount(),
						transaction.getFinalBalance(),
						transaction.getCreatedAt()))
				.toList();
	}
}
