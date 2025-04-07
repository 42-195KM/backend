package com._42195km.msa.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com._42195km.msa.common.api.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = {CustomBusinessException.class})
	public ResponseEntity<ApiResponse<?>> handleBusinessException(CustomBusinessException e) {
		return ResponseEntity
			.status(e.getCode().getStatus())
			.body(ApiResponse.builder()
				.code(e.getCode().getCode())
				.message(e.getCode().getMessage())
				.build());
	}

	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		ApiResponse<?> response = ApiResponse.createValidationErrorResponse(e);
		return ResponseEntity
			.status(response.status())
			.body(response);
	}
}
