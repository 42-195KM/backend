package com._42195km.msa.user.domain.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.exception.code.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserException implements ErrorCode {
	NOT_FOUND_USER_LIST(HttpStatus.NOT_FOUND, "유저 리스트가 비어있습니다.", "E_EMPTY_USER_LIST");

	private final HttpStatus httpStatus;
	private final String message;
	private final String errorCode;

	@Override
	public String getCode() {
		return this.errorCode;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public int getStatus() {
		return this.httpStatus.value();
	}
}
