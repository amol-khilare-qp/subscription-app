package com.questionpro.subscriptionupgrade.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.questionpro.subscriptionupgrade.dto.PaymentRequestDto;
import com.questionpro.subscriptionupgrade.dto.PaymentResponseDto;

@Component
public class PaymentGatewayClient {

	@Value("${payment.gateway.url}")
	private String paymentGatewayUrl;

	@Autowired
	private RestTemplate restTemplate;

	public PaymentResponseDto processPayment(PaymentRequestDto paymentRequest) {

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");

		HttpEntity<PaymentRequestDto> entity = new HttpEntity<>(paymentRequest, headers);
		// Temporarily returning dummy response
		return new PaymentResponseDto("success", "123456", "");

//		ResponseEntity<PaymentResponse> response = restTemplate.exchange(paymentGatewayUrl, HttpMethod.POST, entity,
//				PaymentResponse.class);
//
//		if ("success".equals(response.getBody().getStatus())) {
//			return response.getBody();
//		} else {
//			throw new PaymentFailedException("Payment failed: " + response.getBody().getError());
//		}

	}
}