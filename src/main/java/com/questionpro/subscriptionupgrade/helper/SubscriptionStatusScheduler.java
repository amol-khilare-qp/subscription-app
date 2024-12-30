package com.questionpro.subscriptionupgrade.helper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.questionpro.subscriptionupgrade.service.impl.SubscriptionHelperService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SubscriptionStatusScheduler {

	private final SubscriptionHelperService subscriptionHelperService;

	public SubscriptionStatusScheduler(SubscriptionHelperService subscriptionHelperService) {
		this.subscriptionHelperService = subscriptionHelperService;
	}

	// Scheduler to run daily at midnight at 12:00 AM
	@Scheduled(cron = "0 0 0 * * ?") 
	@Transactional
	public void deactivateExpiredSubscriptions() {
		log.info("Running scheduler to deactivate expired subscriptions...");
		int updatedCount = subscriptionHelperService.deactivateExpiredSubscriptions();
		log.info("Scheduler completed. Total subscriptions deactivated: {}", updatedCount);
	}
}
