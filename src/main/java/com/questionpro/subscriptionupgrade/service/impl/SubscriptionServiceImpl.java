package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.exception.SubscriptionNotFoundException;
import com.questionpro.subscriptionupgrade.exception.UserNotFoundException;
import com.questionpro.subscriptionupgrade.repository.SubscriptionRepository;
import com.questionpro.subscriptionupgrade.repository.UserRepository;
import com.questionpro.subscriptionupgrade.repository.UserSubscriptionRepository;
import com.questionpro.subscriptionupgrade.service.SubscriptionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;
	private final UserSubscriptionRepository userSubscriptionRepository;

	public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository,
			UserSubscriptionRepository userSubscriptionRepository) {
		this.subscriptionRepository = subscriptionRepository;
		this.userRepository = userRepository;
		this.userSubscriptionRepository = userSubscriptionRepository;
	}

	@Override
	@Transactional
	public UserSubscription upgradeSubscription(Long subscriptionId, Long userId) {
		Subscription subscription = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found"));

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

		UserSubscription existingActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUser(user);
		if (existingActiveSubscription != null) {
			existingActiveSubscription.setActive(false);
			userSubscriptionRepository.save(existingActiveSubscription);
		}

		UserSubscription newSubscription = new UserSubscription();
		newSubscription.setUser(user);
		newSubscription.setSubscription(subscription);
		newSubscription.setSubscriptionStartDate(LocalDateTime.now());
		newSubscription.setSubscriptionEndDate(LocalDateTime.now().plusMonths(12));
		newSubscription.setActive(true);

		return userSubscriptionRepository.save(newSubscription);
	}

	@Override
	@Transactional
	public UserSubscription addSubscription(Long userId, Long subscriptionId) {
		log.info("Adding new subscription for user ID: {} with subscription ID: {}", userId, subscriptionId);

		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
		Subscription subscription = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found"));

		if (isUserAlreadyActiveSubscriptionPlan(user, subscription)) {
			throw new RuntimeException("User is already subscribed to this subscription.");
		}

		UserSubscription existingActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUser(user);
		if (existingActiveSubscription != null) {
			existingActiveSubscription.setActive(false);
			userSubscriptionRepository.save(existingActiveSubscription);
		}

		UserSubscription userSubscription = new UserSubscription();
		userSubscription.setUser(user);
		userSubscription.setSubscription(subscription);
		userSubscription.setSubscriptionStartDate(LocalDateTime.now());
		userSubscription.setSubscriptionEndDate(LocalDateTime.now().plusMonths(12));
		userSubscription.setActive(true);

		return userSubscriptionRepository.save(userSubscription);
	}

	private boolean isUserAlreadyActiveSubscriptionPlan(User user, Subscription subscription) {
		UserSubscription existingSubscription = userSubscriptionRepository
				.findActiveSubscriptionByUserAndSubscription(user, subscription);

		return existingSubscription != null
				&& existingSubscription.getSubscriptionEndDate().isAfter(LocalDateTime.now());
	}

}
