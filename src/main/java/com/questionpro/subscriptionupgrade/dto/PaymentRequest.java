package com.questionpro.subscriptionupgrade.dto;

import org.hibernate.validator.constraints.CreditCardNumber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

	@NotNull(message = "Subscription ID is required.")
	private Long subscriptionId;

	@NotBlank(message = "Name on card is required.")
	@Size(max = 100, message = "Name on card should not exceed 100 characters.")
	private String name;

	@NotBlank(message = "Card number is required.")
	@Pattern(regexp = "^[0-9]{16}$", message = "Card number must be a 16-digit numeric string.")
	// @CreditCardNumber
	private String cardNumber;

	@NotBlank(message = "CVV is required.")
	@Pattern(regexp = "^[0-9]{3}$", message = "CVV must be a 3-digit numeric string.")
	private String cvv;

	@NotBlank(message = "Expiry date is required.")
	@Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiry date must be in MM/YY format.")
	private String expiryDate;

	@NotNull(message = "User ID is required.")
	private Long userId;

}
