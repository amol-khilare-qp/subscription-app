package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.repository.UserSubscriptionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubscriptionHelperService {

	private final UserSubscriptionRepository userSubscriptionRepository;

	public SubscriptionHelperService(UserSubscriptionRepository userSubscriptionRepository) {
		this.userSubscriptionRepository = userSubscriptionRepository;
	}

	@Transactional
	public int deactivateExpiredSubscriptions() {
		// Find all active subscriptions that have expired
		List<UserSubscription> expiredSubscriptions = userSubscriptionRepository
				.findExpiredActiveSubscriptions(LocalDateTime.now());

		// Deactivate all expired subscriptions
		expiredSubscriptions.forEach(subscription -> {
			subscription.setActive(false);
			log.info("Deactivated expired subscription: {}", subscription.getId());
		});

		userSubscriptionRepository.saveAll(expiredSubscriptions);

		return expiredSubscriptions.size();
	}
}
