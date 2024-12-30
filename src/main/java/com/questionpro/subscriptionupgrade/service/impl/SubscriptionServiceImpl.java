package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.exception.ActiveSubscriptionException;
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
	public UserSubscription addOrRenewSubscription(Long userId, Long subscriptionId) {
		log.info("Processing subscription addition/renewal for user ID: {} with subscription ID: {}", userId,
				subscriptionId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(
				() -> new SubscriptionNotFoundException("Subscription not found with ID: " + subscriptionId));

		UserSubscription existingActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUser(user);

		if (existingActiveSubscription != null) {
			if (existingActiveSubscription.getSubscription().equals(subscription)) {
				log.info("Renewing existing subscription for user ID: {}", userId);
				return renewSubscription(existingActiveSubscription);
			} else {
				throw new ActiveSubscriptionException("User with ID: " + userId
						+ " already has an active subscription with a different plan. Please use the upgrade option.");
			}
		}

		return createNewSubscription(user, subscription);
	}

	@Override
	@Transactional
	public UserSubscription upgradeSubscription(Long userId, Long newSubscriptionId) {
		log.info("Processing subscription upgrade for user ID: {} to new subscription ID: {}", userId,
				newSubscriptionId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		Subscription newSubscription = subscriptionRepository.findById(newSubscriptionId).orElseThrow(
				() -> new SubscriptionNotFoundException("Subscription not found with ID: " + newSubscriptionId));

		UserSubscription existingActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUser(user);

		if (existingActiveSubscription == null) {
			throw new ActiveSubscriptionException(
					"User with ID: " + userId + " has no active subscription to upgrade.");
		}

		if (existingActiveSubscription.getSubscription().equals(newSubscription)) {
			throw new ActiveSubscriptionException(
					"User with ID: " + userId + " is already subscribed to the same plan.");
		}

		existingActiveSubscription.setActive(false);
		userSubscriptionRepository.save(existingActiveSubscription);

		return createNewSubscription(user, newSubscription);
	}

	private UserSubscription renewSubscription(UserSubscription existingSubscription) {
		log.info("Renewing subscription ID: {} for user ID: {}", existingSubscription.getId(),
				existingSubscription.getUser().getUserId());

		existingSubscription.setSubscriptionEndDate(existingSubscription.getSubscriptionEndDate().plusMonths(12));
		return userSubscriptionRepository.save(existingSubscription);
	}

	private UserSubscription createNewSubscription(User user, Subscription subscription) {
		log.info("Creating new subscription for user ID: {} with subscription ID: {}", user.getUserId(),
				subscription.getSubscriptionId());

		UserSubscription newSubscription = new UserSubscription();
		newSubscription.setUser(user);
		newSubscription.setSubscription(subscription);
		newSubscription.setSubscriptionStartDate(LocalDateTime.now());
		newSubscription.setSubscriptionEndDate(LocalDateTime.now().plusMonths(12));
		newSubscription.setActive(true);

		return userSubscriptionRepository.save(newSubscription);
	}
}
