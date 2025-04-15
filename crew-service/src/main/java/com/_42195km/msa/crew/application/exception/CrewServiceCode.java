package com._42195km.msa.crew.application.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum CrewServiceCode implements ServiceCode {
	CREW_CREATE_POST_SUCCESS("CREW_001", "크루 생성 성공", HttpStatus.OK),
	CREW_NAME_DUPLICATED("CREW_002", "크루 이름은 중복될 수 없습니다", HttpStatus.BAD_REQUEST)
	;

	private final String code;
	private final String message;
	private final int status;

	CrewServiceCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status.value();
	}
}

