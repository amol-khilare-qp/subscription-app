package com.questionpro.subscriptionupgrade.exception;

public class SubscriptionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 5517765998902117558L;

	public SubscriptionNotFoundException(String message) {
		super(message);
	}
}
