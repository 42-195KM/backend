package com._42195km.msa.auth.infrastructure.filter.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FilterException implements ServiceCode {
	NOT_FOUND_AUTH_USER(HttpStatus.NOT_FOUND, "Auth 서비스에 저장된 유저를 찾지 못했습니다.", "E_NOT_FOUND_AUTH_USER"),
	LOGIN_FAILED_WORNG_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다.", "E_LOGIN_FAILED_WORNG_PASSWORD"),
	;

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
