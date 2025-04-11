package com_42195km.msa.achievementservice.infrastructure.config;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum AchievementServiceCode implements ServiceCode {
	ACHIEVEMENT_CREATE_SUCCESS("ACHIEVEMENT_CREATE_SUCCESS",
		"업적이 성공적으로 생성했습니다.", HttpStatus.OK.value()),
	ACHIEVEMENT_CREATE_FAIL("ACHIEVEMENT_CREATE_FAIL",
		"업적 생성이 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIEVEMENT_GET_SUCCESS("ACHIEVEMENT_GET_SUCCESS",
		"업적이 성공적으로 가져왔습니다.", HttpStatus.OK.value()),
	ACHIEVEMENT_GET_FAIL("ACHIEVEMENT_GET_FAIL",
		"업적 가져오기가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIEVEMENT_GET_ALL_SUCCESS("ACHIEVEMENT_GET_ALL_SUCCESS",
		"모든 업적을 성공적으로 가져왔습니다.", HttpStatus.OK.value()),
	ACHIEVEMENT_GET_ALL_FAIL("ACHIEVEMENT_GET_ALL_FAIL",
		"모든 업적 가져오기가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIEVEMENT_SEARCH_SUCCESS("ACHIEVEMENT_SEARCH_SUCCESS",
		"업적을 성공적으로 검색하였습니다.", HttpStatus.OK.value()),
	ACHIEVEMENT_SEARCH_FAIL("ACHIEVEMENT_SEARCH_FAIL",
		"업적 검색이 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIEVEMENT_GET_BY_USER_SUCCESS("ACHIEVEMENT_GET_BY_USER_SUCCESS",
		"사용자의 업적을 성공적으로 가져왔습니다.", HttpStatus.OK.value()),
	ACHIEVEMENT_GET_BY_USER_FAIL("ACHIEVEMENT_GET_BY_USER_FAIL",
		"사용자의 업적 가져오기가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	ACHIEVEMENT_DELETE_SUCCESS("ACHIEVEMENT_DELETE_SUCCESS",
		"업적을 성공적으로 삭제하습니다.", HttpStatus.OK.value()),
	ACHIEVEMENT_DELETE_FAIL("ACHIEVEMENT_DELETE_FAIL",
		"업적 삭제가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());

	private final String code;
	private final String message;
	private final int status;

	AchievementServiceCode(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
