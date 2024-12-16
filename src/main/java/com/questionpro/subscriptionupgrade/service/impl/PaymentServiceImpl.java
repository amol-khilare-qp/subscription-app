package com.questionpro.subscriptionupgrade.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.questionpro.subscriptionupgrade.client.ApigatewayClient;
import com.questionpro.subscriptionupgrade.dto.PaymentRequest;
import com.questionpro.subscriptionupgrade.dto.PaymentResponse;
import com.questionpro.subscriptionupgrade.entity.Subscription;
import com.questionpro.subscriptionupgrade.entity.User;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.exception.PaymentFailedException;
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

	@Autowired
	ApigatewayClient apigatewayClient;

	public PaymentServiceImpl(SubscriptionRepository subscriptionRepository, UserRepository userRepository,
			UserSubscriptionRepository userSubscriptionRepository) {
		this.subscriptionRepository = subscriptionRepository;
		this.userRepository = userRepository;
		this.userSubscriptionRepository = userSubscriptionRepository;
	}

	@Override
	public boolean processPayment(PaymentRequest paymentRequest) {
		log.info("Processing payment for card number: {} (name: {})", paymentRequest.getCardNumber(),
				paymentRequest.getName());

		try {
			Subscription subscription = subscriptionRepository.findById(paymentRequest.getSubscriptionId())
					.orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found"));

			User user = userRepository.findById(paymentRequest.getUserId())
					.orElseThrow(() -> new UserNotFoundException("User not found"));

			if (isUserAlreadyActiveSubscriptionPlan(user, subscription)) {
				throw new RuntimeException("User is already subscribed to this subscription.");
			}

			// We call here payment gateway third party api
			PaymentResponse processPayment = apigatewayClient.processPayment(paymentRequest);
			if (processPayment.getStatus().equalsIgnoreCase("Success")) {
				log.info("Payment processed successfully for card number: {}", paymentRequest.getCardNumber());
				return true;
			}
			return false;

		} catch (PaymentFailedException e) {
			throw e;
		} catch (Exception e) {
			throw new PaymentFailedException("An error occurred while processing payment.");
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
