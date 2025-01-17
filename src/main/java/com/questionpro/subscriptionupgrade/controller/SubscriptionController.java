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
import com.questionpro.subscriptionupgrade.util.ResponseUtil;

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
    public ResponseEntity<BaseResponse<String>> upgradeSubscription(@Valid @RequestBody PaymentRequestDto paymentRequestDto) {
        UserSubscription updatedSubscription = subscriptionService.upgradeSubscription(paymentRequestDto);
        String responseMessage = "Subscription upgraded successfully.";
        String responseObject = "Subscription upgraded successfully with ID: " + updatedSubscription.getId();
        return ResponseUtil.createSuccessResponse(responseMessage, responseObject);
    }


}

