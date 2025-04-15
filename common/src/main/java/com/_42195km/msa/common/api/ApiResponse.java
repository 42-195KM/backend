package com._42195km.msa.common.api;

import java.util.List;

import org.springframework.web.bind.MethodArgumentNotValidException;

import com._42195km.msa.common.code.CommonServiceCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

public record ApiResponse<T>(
	String code,
	@JsonInclude(JsonInclude.Include.NON_NULL)
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
			.code(CommonServiceCode.METHOD_ARGUMENT_NOT_VALID.getCode())
			.data(errorMessages)
			.message(CommonServiceCode.METHOD_ARGUMENT_NOT_VALID.getMessage())
			.status(CommonServiceCode.METHOD_ARGUMENT_NOT_VALID.getStatus())
			.build();
	}

	public static <T> ApiResponse<T> success(T data){
		return ApiResponse.<T>builder()
				.code(CommonServiceCode.SUCCESS.getCode())
				.message(CommonServiceCode.SUCCESS.getMessage())
				.data(data)
				.build();
	}

	public static <T> ApiResponse<T> success(T data, String message){
		return ApiResponse.<T>builder()
				.code(CommonServiceCode.SUCCESS.getCode())
				.data(data)
				.message(message)
				.build();
	}

}
