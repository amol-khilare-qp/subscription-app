package com.questionpro.subscriptionupgrade.service;

import com.questionpro.subscriptionupgrade.dto.PaymentRequestDto;

public interface PaymentService {

	boolean processPayment(PaymentRequestDto paymentRequest);

}
