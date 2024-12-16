package com.questionpro.subscriptionupgrade.service;

import com.questionpro.subscriptionupgrade.dto.PaymentRequest;

public interface PaymentService {

	boolean processPayment(PaymentRequest paymentRequest);

}
