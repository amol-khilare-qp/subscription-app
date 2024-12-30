package com.questionpro.subscriptionupgrade.exception;

public class ActiveSubscriptionException extends RuntimeException {

	private static final long serialVersionUID = -8132089948208014503L;

	public ActiveSubscriptionException(String message) {
		super(message);
	}
}
