package com.questionpro.subscriptionupgrade.service;

import com.questionpro.subscriptionupgrade.dto.PaymentRequestDto;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;

public interface SubscriptionService {
	UserSubscription updateSubscription(PaymentRequestDto paymentRequest);
}
