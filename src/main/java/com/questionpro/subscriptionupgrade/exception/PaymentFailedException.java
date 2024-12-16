package com.questionpro.subscriptionupgrade.exception;

public class PaymentFailedException extends RuntimeException {

	private static final long serialVersionUID = 4117852019175714827L;

	public PaymentFailedException(String message) {
		super(message);
	}
}
