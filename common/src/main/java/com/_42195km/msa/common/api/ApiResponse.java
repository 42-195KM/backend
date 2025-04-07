package com._42195km.msa.common.api;

import java.util.List;

import org.springframework.web.bind.MethodArgumentNotValidException;

import com._42195km.msa.common.exception.code.CommonErrorCode;

import lombok.Builder;

public record ApiResponse<T>(
	String code,
	T data,
	String message,
	int status
) {

	@Builder
	public ApiResponse {
	}

	public static ApiResponse<?> createValidationErrorResponse(MethodArgumentNotValidException e) {
		List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
			.map(fieldError -> {
				var format = "필드 - {%s} 입력값 - {%s} 메시지- {%s}";
				return String.format(format, fieldError.getField(), fieldError.getRejectedValue(),
					fieldError.getDefaultMessage());
			}).toList();

		return ApiResponse.builder()
			.code(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getCode())
			.data(errorMessages)
			.message(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getMessage())
			.status(CommonErrorCode.METHOD_ARGUMENT_NOT_VALID.getStatus())
			.build();
	}
}
