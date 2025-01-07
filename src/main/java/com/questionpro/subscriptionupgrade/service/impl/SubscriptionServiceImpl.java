package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.questionpro.subscriptionupgrade.dto.PaymentRequest;
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
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final int SUBSCRIPTION_DURATION_MONTHS = 12;

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
	@Transactional
	public UserSubscription addOrRenewSubscription(PaymentRequest paymentRequest) {
		log.info("Processing subscription addition/renewal for user ID: {} with subscription ID: {}, at {}",
				paymentRequest.getUserId(), paymentRequest.getSubscriptionId(), LocalDateTime.now());

		User user = userRepository.findById(paymentRequest.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + paymentRequest.getUserId()));

		Subscription subscription = subscriptionRepository.findById(paymentRequest.getSubscriptionId())
				.orElseThrow(() -> new SubscriptionNotFoundException(
						"Subscription not found with ID: " + paymentRequest.getSubscriptionId()));

		if (isUserAlreadyActiveSubscriptionPlan(user, subscription)) {
			log.error("User is already subscribed to this subscription: {}", paymentRequest.getUserId());
			throw new ActiveSubscriptionException("User is already subscribed to this subscription.");
		}

		boolean paymentSuccess = paymentService.processPayment(paymentRequest);

		if (!paymentSuccess) {
			log.error("Payment failed for user ID: {} with subscription ID: {}", paymentRequest.getUserId(),
					paymentRequest.getSubscriptionId());
			throw new PaymentFailedException("Payment failed. Please try again later.");
		}

		UserSubscription existingActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUser(user);

		if (existingActiveSubscription != null) {
			if (existingActiveSubscription.getSubscription().equals(subscription)) {
				log.info("Renewing existing subscription for user ID: {}", paymentRequest.getUserId());
				return renewSubscription(existingActiveSubscription);
			} else {
				log.warn("User with ID: {} already has an active subscription with a different plan.",
						paymentRequest.getUserId());
				throw new ActiveSubscriptionException("User with ID: " + paymentRequest.getUserId()
						+ " already has an active subscription with a different plan. Please use the upgrade option.");
			}
		}

		return createNewSubscription(user, subscription);
	}

	@Override
	@Transactional
	public UserSubscription upgradeSubscription(PaymentRequest paymentRequest) {
		log.info("Processing subscription upgrade for user ID: {} to new subscription ID: {}, at {}",
				paymentRequest.getUserId(), paymentRequest.getSubscriptionId(), LocalDateTime.now());

		User user = userRepository.findById(paymentRequest.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + paymentRequest.getUserId()));

		Subscription subscription = subscriptionRepository.findById(paymentRequest.getSubscriptionId())
				.orElseThrow(() -> new SubscriptionNotFoundException(
						"Subscription not found with ID: " + paymentRequest.getSubscriptionId()));

		if (isUserAlreadyActiveSubscriptionPlan(user, subscription)) {
			log.error("User is already subscribed to this subscription: {}", paymentRequest.getUserId());
			throw new ActiveSubscriptionException("User is already subscribed to this subscription.");
		}

		boolean paymentSuccess = paymentService.processPayment(paymentRequest);

		if (!paymentSuccess) {
			log.error("Payment failed for user ID: {} while upgrading to subscription ID: {}",
					paymentRequest.getUserId(), paymentRequest.getSubscriptionId());
			throw new PaymentFailedException("Payment failed. Please try again later.");
		}

		UserSubscription existingActiveSubscription = userSubscriptionRepository.findActiveSubscriptionByUser(user);

		if (existingActiveSubscription == null) {
			log.warn("User with ID: {} has no active subscription to upgrade.", paymentRequest.getUserId());
			throw new ActiveSubscriptionException(
					"User with ID: " + paymentRequest.getUserId() + " has no active subscription to upgrade.");
		}

		if (existingActiveSubscription.getSubscription().equals(subscription)) {
			log.warn("User with ID: {} is already subscribed to the same plan.", paymentRequest.getUserId());
			throw new ActiveSubscriptionException(
					"User with ID: " + paymentRequest.getUserId() + " is already subscribed to the same plan.");
		}

		existingActiveSubscription.setActive(Boolean.FALSE);
		userSubscriptionRepository.save(existingActiveSubscription);

		return createNewSubscription(user, subscription);
	}

	private UserSubscription renewSubscription(UserSubscription existingSubscription) {
		log.info("Renewing subscription ID: {} for user ID: {}", existingSubscription.getId(),
				existingSubscription.getUser().getUserId());

		existingSubscription.setSubscriptionEndDate(
				existingSubscription.getSubscriptionEndDate().plusMonths(SUBSCRIPTION_DURATION_MONTHS));
		return userSubscriptionRepository.save(existingSubscription);
	}

	private UserSubscription createNewSubscription(User user, Subscription subscription) {
		log.info("Creating new subscription for user ID: {} with subscription ID: {}", user.getUserId(),
				subscription.getSubscriptionId());

		UserSubscription newSubscription = new UserSubscription();
		newSubscription.setUser(user);
		newSubscription.setSubscription(subscription);
		newSubscription.setSubscriptionStartDate(LocalDateTime.now());
		newSubscription.setSubscriptionEndDate(LocalDateTime.now().plusMonths(SUBSCRIPTION_DURATION_MONTHS));
		newSubscription.setActive(true);

		return userSubscriptionRepository.save(newSubscription);
	}

	private boolean isUserAlreadyActiveSubscriptionPlan(User user, Subscription subscription) {
		UserSubscription existingSubscription = userSubscriptionRepository.findByUserAndSubscription(user,
				subscription);
		if (existingSubscription != null
				&& existingSubscription.getSubscriptionEndDate().isAfter(LocalDateTime.now())) {
			log.info("User already has an active subscription.");
			return true;
		}
		return false;
	}
}
