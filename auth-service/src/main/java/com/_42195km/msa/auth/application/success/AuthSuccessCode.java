package com._42195km.msa.auth.application.success;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuthSuccessCode implements ServiceCode {

	LOGIN_SUCCESS(HttpStatus.OK, "로그인이 성공했습니다.", "S_LOGIN_SUCCESS"),
	REFRESH_SUCCESS(HttpStatus.OK, "AccessToken 재발급이 성공했습니다.", "S_REFRESH_SUCCESS"),
	LOGOUT_SUCCESS(HttpStatus.NO_CONTENT, "로그아웃을 성공했습니다.", "S_LOGOUT_SUCCESS"),
	BLACK_LIST_SUCCESS(HttpStatus.NO_CONTENT, "블랙리스트 처리를 성공했습니다.", "S_BLACK_LIST_SUCCESS"),
	VALIDATE_TOKEN_SUCCESS(HttpStatus.OK, "토큰 검증이 성공했습니다.", "S_VALIDATE_TOKEN_SUCCESS"),
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
