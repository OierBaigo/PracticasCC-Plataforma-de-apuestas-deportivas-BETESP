package com.match_service.exception;

public class MatchNotExistsException extends RuntimeException {
	public MatchNotExistsException(String message) {
	    super(message);
	}
}
