package com.questionpro.subscriptionupgrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
	private String status;
	private String transactionId;
	private String error;
}
