package com.questionpro.subscriptionupgrade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.questionpro.subscriptionupgrade.dto.BaseResponse;
import com.questionpro.subscriptionupgrade.dto.PaymentRequestDto;
import com.questionpro.subscriptionupgrade.entity.UserSubscription;
import com.questionpro.subscriptionupgrade.service.SubscriptionService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

	private final SubscriptionService subscriptionService;

	public SubscriptionController(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	@PostMapping("/upgrade")
	public ResponseEntity<BaseResponse<String>> upgradeSubscription(@Valid @RequestBody PaymentRequestDto paymentRequest) {
		log.info("Received request to upgrade subscription for user ID: {}", paymentRequest.getUserId());

		UserSubscription updatedSubscription = subscriptionService.upgradeSubscription(paymentRequest);

		BaseResponse<String> successResponse = new BaseResponse<>();
		successResponse.setStatus("SUCCESS");
		successResponse.setMessage("Subscription upgraded successfully.");
		successResponse.setResponseObject("Subscription upgraded successfully with ID: " + updatedSubscription.getId());
		return ResponseEntity.ok().body(successResponse);
	}

	@PostMapping("/add")
	public ResponseEntity<BaseResponse<String>> addSubscription(@Valid @RequestBody PaymentRequestDto paymentRequest) {
		log.info("Received request to add subscription for user ID: {}", paymentRequest.getUserId());

		UserSubscription newSubscription = subscriptionService.addOrRenewSubscription(paymentRequest);

		BaseResponse<String> successResponse = new BaseResponse<>();
		successResponse.setStatus("SUCCESS");
		successResponse.setResponseObject("Subscription added successfully with ID: " + newSubscription.getId());
		return ResponseEntity.ok().body(successResponse);
	}

}
