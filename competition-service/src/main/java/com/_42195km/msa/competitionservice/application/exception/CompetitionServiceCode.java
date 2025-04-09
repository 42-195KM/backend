package com._42195km.msa.competitionservice.application.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum CompetitionServiceCode implements ServiceCode {

	COMPETITION_CREATE_SUCCESS("CPT_001", "대회 생성 성공", HttpStatus.OK),
	COMPETITION_CREATE_FAIL("CPT_002", "대회 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	COMPETITION_GET_SUCCESS("CPT_003", "대회 조회 성공", HttpStatus.OK),
	COMPETITION_GET_FAIL("CPT_004", "대회 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	;

	private final String code;
	private final String message;
	private final int status;

	CompetitionServiceCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status.value();
	}
}
