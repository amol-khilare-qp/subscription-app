package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.questionpro.subscriptionupgrade.client.ApigatewayClient;
import com.questionpro.subscriptionupgrade.dto.PaymentRequest;
import com.questionpro.subscriptionupgrade.dto.PaymentResponse;
import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.exception.SubscriptionNotFoundException;
import com.questionpro.subscriptionupgrade.exception.UserNotFoundException;
import com.questionpro.subscriptionupgrade.repository.SubscriptionRepository;
import com.questionpro.subscriptionupgrade.repository.UserRepository;
import com.questionpro.subscriptionupgrade.repository.UserSubscriptionRepository;
import com.questionpro.subscriptionupgrade.service.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;
	private final UserSubscriptionRepository userSubscriptionRepository;
	private final ApigatewayClient apigatewayClient;

	public PaymentServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository,
			UserSubscriptionRepository userSubscriptionRepository, ApigatewayClient apigatewayClient) {
		this.subscriptionRepository = subscriptionRepository;
		this.userRepository = userRepository;
		this.userSubscriptionRepository = userSubscriptionRepository;
		this.apigatewayClient = apigatewayClient;
	}

	@Override
	public boolean processPayment(PaymentRequest paymentRequest) {
		log.info("Processing payment for card number: {} (name: {})", paymentRequest.getCardNumber(),
				paymentRequest.getName());

		Subscription subscription = subscriptionRepository.findById(paymentRequest.getSubscriptionId())
				.orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found"));

		User user = userRepository.findById(paymentRequest.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		if (isUserAlreadyActiveSubscriptionPlan(user, subscription)) {
			log.error("User is already subscribed to this subscription: {}", paymentRequest.getUserId());
			throw new RuntimeException("User is already subscribed to this subscription.");
		}

		// Call to the payment gateway (third-party API)
		PaymentResponse paymentResponse = apigatewayClient.processPayment(paymentRequest);
		if ("success".equalsIgnoreCase(paymentResponse.getStatus())) {
			log.info("Payment processed successfully for user: {}", paymentRequest.getUserId());
			return true;
		} else {
			log.error("Payment failed for user {}: {}", paymentRequest.getUserId(), paymentResponse.getError());
			return false;
		}
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
