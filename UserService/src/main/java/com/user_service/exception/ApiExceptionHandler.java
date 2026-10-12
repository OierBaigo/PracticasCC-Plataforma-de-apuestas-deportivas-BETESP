package com.user_service.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<Map<String,String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensaje", ex.getMessage()));
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", mensaje));
    }
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String,String>> handleInvalidCredentialsException(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", ex.getMessage()));
	}
	
	@ExceptionHandler(UserNotExistsException.class)
	public ResponseEntity<Map<String,String>> handleUserNotExistsException(UserNotExistsException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", ex.getMessage()));
	}
	
	@ExceptionHandler(InsufficientBalanceException.class)
	public ResponseEntity<Map<String,String>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
	}
}
