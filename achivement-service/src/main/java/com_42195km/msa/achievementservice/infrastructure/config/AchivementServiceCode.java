package com_42195km.msa.achievementservice.infrastructure.config;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum AchivementServiceCode implements ServiceCode {
	ACHIVEMENT_CREATE_SUCCESS("ACHIVEMENT_CREATE_SUCCESS",
		"업적이 성공적으로 생성했습니다.", HttpStatus.OK.value()),
	ACHIVEMENT_CREATE_FAIL("ACHIVEMENT_CREATE_FAIL",
		"업적 생성이 실패하였습니다.", HttpStatus.OK.value());

	private final String code;
	private final String message;
	private final int status;

	AchivementServiceCode(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
