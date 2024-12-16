package com.questionpro.subscriptionupgrade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.questionpro.subscriptionupgrade.dto.BaseResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// Handle validation exceptions (from @Valid)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		// Collect all validation errors
		StringBuilder errorMessage = new StringBuilder("Validation failed for the following fields: ");
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
		}

		// Create and return the response
		BaseResponse<String> errorResponse = new BaseResponse<>();
		errorResponse.setStatus("FAILURE");
		errorResponse.setMessage(errorMessage.toString());
		errorResponse.setReasonCode("VALIDATION_FAILED");

		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	// Handle SubscriptionNotFoundException
	@ExceptionHandler(SubscriptionNotFoundException.class)
	public ResponseEntity<BaseResponse<String>> handleSubscriptionNotFoundException(SubscriptionNotFoundException ex) {
		log.error("Subscription error: {}", ex.getMessage());
		BaseResponse<String> errorResponse = new BaseResponse<>();
		errorResponse.setStatus("FAILURE");
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setReasonCode("SUBSCRIPTION_NOT_FOUND");

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// Handle UserNotFoundException
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<BaseResponse<String>> handleUserNotFoundException(UserNotFoundException ex) {
		log.error("User error: {}", ex.getMessage());
		BaseResponse<String> errorResponse = new BaseResponse<>();
		errorResponse.setStatus("FAILURE");
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setReasonCode("USER_NOT_FOUND");

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	// Handle generic exception (for other unhandled cases)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<String>> handleGenericException(Exception ex) {
		log.error("An error occurred: {}", ex.getMessage());
		BaseResponse<String> errorResponse = new BaseResponse<>();
		errorResponse.setStatus("FAILURE");
		errorResponse.setMessage("Internal Server Error");
		errorResponse.setReasonCode("INTERNAL_SERVER_ERROR");

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Handle PaymentFailedException
	@ExceptionHandler(PaymentFailedException.class)
	public ResponseEntity<BaseResponse<String>> handlePaymentFailedException(PaymentFailedException ex) {
		log.error("Payment error: {}", ex.getMessage());
		BaseResponse<String> errorResponse = new BaseResponse<>();
		errorResponse.setStatus("FAILURE");
		errorResponse.setMessage(ex.getMessage());
		errorResponse.setReasonCode("PAYMENT_FAILED");

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
