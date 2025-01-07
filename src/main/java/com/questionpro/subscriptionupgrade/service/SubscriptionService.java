package com.questionpro.subscriptionupgrade.service;

import com.questionpro.subscriptionupgrade.dto.PaymentRequest;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;

public interface SubscriptionService {
	UserSubscription upgradeSubscription(PaymentRequest paymentRequest);

	UserSubscription addOrRenewSubscription(PaymentRequest paymentRequest);
}
