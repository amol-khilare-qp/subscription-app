package com.questionpro.subscriptionupgrade.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BaseResponse<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private String status;
	private String reasonCode;
	private String requestId;
	private String message;
	private List<T> responseListObject;
	private T responseObject;
}
