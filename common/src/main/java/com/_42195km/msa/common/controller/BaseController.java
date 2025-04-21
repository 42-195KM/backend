package com._42195km.msa.common.controller;

import org.springframework.http.ResponseEntity;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.common.code.ServiceCode;

public abstract class BaseController {
	protected  <T> ResponseEntity<ApiResponse<T>> createOkResponseEntity(T data, ServiceCode code) {
		return ResponseEntity.ok(ApiResponse.<T>builder()
			.code(code.getCode())
			.message(code.getMessage())
			.status(code.getStatus())
			.data(data)
			.build());
	}
}
