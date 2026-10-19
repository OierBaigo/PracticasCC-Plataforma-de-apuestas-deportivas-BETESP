package com.match_service.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(MatchNotExistsException.class)
	public ResponseEntity<Map<String,String>> handleMatchNotExistsException(MatchNotExistsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
	}
}

