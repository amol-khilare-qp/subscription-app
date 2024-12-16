package com.questionpro.subscriptionupgrade.exception;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5494008820623386808L;

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
