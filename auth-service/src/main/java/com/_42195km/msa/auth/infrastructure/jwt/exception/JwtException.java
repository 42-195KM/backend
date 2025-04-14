package com._42195km.msa.auth.infrastructure.jwt.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtException implements ServiceCode {
	INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.", "E_INVALID_JWT_SIGNATURE"),
	EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다.", "E_EXPIRED_JWT_TOKEN"),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.", "E_UNSUPPORTED_JWT_TOKEN"),
	JWT_CLAIM_IS_EMPTY(HttpStatus.UNAUTHORIZED, "JWT 토큰이 비어있습니다.", "E_JWT_CLAIM_IS_EMPTY");

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
