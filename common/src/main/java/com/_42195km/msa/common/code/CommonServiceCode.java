package com._42195km.msa.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommonServiceCode implements ServiceCode {
	// 정상 흐름
	SUCCESS("COM_001", "요청 성공", HttpStatus.OK),

	// 예외 흐름
	METHOD_ARGUMENT_NOT_VALID("COM_010", "Validation 실패", HttpStatus.BAD_REQUEST),
	FORBIDDEN("COM_011", "권한 없음", HttpStatus.FORBIDDEN),
	;

	private final String code;
	private final String message;
	private final int status;

	CommonServiceCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status.value();
	}
}
