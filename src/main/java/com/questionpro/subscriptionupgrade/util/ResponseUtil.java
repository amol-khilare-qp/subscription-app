package com.questionpro.subscriptionupgrade.util;

import org.springframework.http.ResponseEntity;

import com.questionpro.subscriptionupgrade.dto.BaseResponse;

public class ResponseUtil {

	public static ResponseEntity<BaseResponse<String>> createSuccessResponse(String message, String responseObject) {
		BaseResponse<String> successResponse = new BaseResponse<>();
		successResponse.setStatus("SUCCESS");
		successResponse.setMessage(message);
		successResponse.setResponseObject(responseObject);
		return ResponseEntity.ok().body(successResponse);
	}

}
