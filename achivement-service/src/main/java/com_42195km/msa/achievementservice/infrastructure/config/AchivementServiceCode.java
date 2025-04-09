package com_42195km.msa.achievementservice.infrastructure.config;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum AchivementServiceCode implements ServiceCode {
	ACHIVEMENT_CREATE_SUCCESS("ACHIVEMENT_CREATE_SUCCESS",
		"업적이 성공적으로 생성했습니다.", HttpStatus.OK.value()),
	ACHIVEMENT_CREATE_FAIL("ACHIVEMENT_CREATE_FAIL",
		"업적 생성이 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIVEMENT_GET_SUCCESS("ACHIVEMENT_GET_SUCCESS",
		"업적이 성공적으로 가져왔습니다.", HttpStatus.OK.value()),
	ACHIVEMENT_GET_FAIL("ACHIVEMENT_GET_FAIL",
		"업적 가져오기가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIVEMENT_GET_ALL_SUCCESS("ACHIVEMENT_GET_ALL_SUCCESS",
		"모든 업적을 성공적으로 가져왔습니다.", HttpStatus.OK.value()),
	ACHIVEMENT_GET_ALL_FAIL("ACHIVEMENT_GET_ALL_FAIL",
		"모든 업적 가져오기가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIVEMENT_SEARCH_SUCCESS("ACHIVEMENT_SEARCH_SUCCESS",
		"업적을 성공적으로 검색하였습니다.", HttpStatus.OK.value()),
	ACHIVEMENT_SEARCH_FAIL("ACHIVEMENT_SEARCH_FAIL",
		"업적 검색이 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());

	private final String code;
	private final String message;
	private final int status;

	AchivementServiceCode(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
