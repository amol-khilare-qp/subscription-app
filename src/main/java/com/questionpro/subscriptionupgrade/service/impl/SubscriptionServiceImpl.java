package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.questionpro.subscriptionupgrade.dto.PaymentRequestDto;
import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.exception.ActiveSubscriptionException;
import com.questionpro.subscriptionupgrade.exception.PaymentFailedException;
import com.questionpro.subscriptionupgrade.exception.SubscriptionNotFoundException;
import com.questionpro.subscriptionupgrade.exception.UserNotFoundException;
import com.questionpro.subscriptionupgrade.repository.SubscriptionRepository;
import com.questionpro.subscriptionupgrade.repository.UserRepository;
import com.questionpro.subscriptionupgrade.repository.UserSubscriptionRepository;
import com.questionpro.subscriptionupgrade.service.PaymentService;
import com.questionpro.subscriptionupgrade.service.SubscriptionService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;
	private final UserSubscriptionRepository userSubscriptionRepository;
	private final PaymentService paymentService;

	public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository,
			UserSubscriptionRepository userSubscriptionRepository, PaymentService paymentService) {
		this.subscriptionRepository = subscriptionRepository;
		this.userRepository = userRepository;
		this.userSubscriptionRepository = userSubscriptionRepository;
		this.paymentService = paymentService;
	}

	@Override
	public UserSubscription upgradeSubscription(PaymentRequestDto paymentRequest) {
		log.info("Processing subscription upgrade for user ID: {} to subscription ID: {}", paymentRequest.getUserId(),
				paymentRequest.getSubscriptionId());

		User user = findUserById(paymentRequest.getUserId());
		Subscription subscription = findSubscriptionById(paymentRequest.getSubscriptionId());
		UserSubscription existingSubscription = findExistingSubscription(user, subscription);
		validateActiveSubscription(existingSubscription);
		processPayment(paymentRequest);
		return handleSubscriptionRenewal(existingSubscription, subscription, user);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
	}

	private Subscription findSubscriptionById(Long subscriptionId) {
		return subscriptionRepository.findById(subscriptionId).orElseThrow(
				() -> new SubscriptionNotFoundException("Subscription not found with ID: " + subscriptionId));
	}

	private UserSubscription findExistingSubscription(User user, Subscription subscription) {
		return userSubscriptionRepository.findByUserAndSubscription(user, subscription);
	}

	private void validateActiveSubscription(UserSubscription existingSubscription) {
		if (existingSubscription != null
				&& existingSubscription.getSubscriptionEndDate().isAfter(LocalDateTime.now())) {
			log.error("User is already subscribed to this plan.");
			throw new ActiveSubscriptionException("User is already subscribed to this plan.");
		}
	}

	private void processPayment(PaymentRequestDto paymentRequest) {
		boolean paymentSuccess = paymentService.processPayment(paymentRequest);
		if (!paymentSuccess) {
			log.error("Payment failed for user ID: {}", paymentRequest.getUserId());
			throw new PaymentFailedException("Payment failed. Please try again.");
		}
	}

	private UserSubscription handleSubscriptionRenewal(UserSubscription existingSubscription, Subscription subscription,
			User user) {
		if (existingSubscription != null) {
			log.info("Renewing expired subscription for user ID: {}", user.getUserId());
			return renewExistingSubscription(existingSubscription, subscription);
		}
		log.error("No existing subscription found for user ID: {} and subscription ID: {}", user.getUserId(),
				subscription.getSubscriptionId());
		throw new SubscriptionNotFoundException("No existing subscription found to update.");
	}

	private UserSubscription renewExistingSubscription(UserSubscription existingSubscription,
			Subscription subscription) {
		log.info("Renewing subscription ID: {} for user ID: {}", existingSubscription.getId(),
				existingSubscription.getUser().getUserId());

		existingSubscription
				.setSubscriptionEndDate(existingSubscription.getSubscriptionEndDate().isAfter(LocalDateTime.now())
						? existingSubscription.getSubscriptionEndDate()
								.plusMonths(subscription.getSubscriptionDuration())
						: LocalDateTime.now().plusMonths(subscription.getSubscriptionDuration()));
		existingSubscription.setActive(true);

		return userSubscriptionRepository.save(existingSubscription);
	}
}
