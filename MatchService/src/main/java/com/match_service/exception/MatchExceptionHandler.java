package com.match_service.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class MatchExceptionHandler {
	@ExceptionHandler(MatchNotExistsException.class)
	public ResponseEntity<Map<String,String>> handleMatchNotExistsException(MatchNotExistsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", ex.getMessage()));
	}
	
	@ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleRemoteError(RestClientResponseException ex) {

        HttpStatus status = ex.getStatusCode().value() == 404
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_GATEWAY;

        return ResponseEntity.status(status).body(Map.of("mensaje", "No se pudo completar la operación con un servicio interno"));
    }
}

