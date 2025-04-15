package com._42195km.msa.common.aop;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionException implements ServiceCode {

	ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "접근이 거부되었습니다.", "E_ACCESS_DENIED"),
	DIFFERNT_PERMISSION(HttpStatus.FORBIDDEN, "요구된 권한과 일치하지 않습니다.", "E_DIFFERNT_PERMISSION"),
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

