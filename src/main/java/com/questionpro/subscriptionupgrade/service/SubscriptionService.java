package com.questionpro.subscriptionupgrade.service;

import com.questionpro.subscriptionupgrade.entity.UserSubscription;

public interface SubscriptionService {
	UserSubscription upgradeSubscription(Long subscriptionId, Long userId);

	UserSubscription addOrRenewSubscription(Long subscriptionId, Long userId);
}
