package com_42195km.msa.runningrecordservice.infrastructure.config;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.exception.code.ErrorCode;

import lombok.Getter;

@Getter
public enum RunningRecordServiceCode implements ErrorCode {
	RUNNING_RECORD_CREATE_SUCCESS("RUNNING_RECORD_CREATE_SUCCESS",
		"러닝 기록이 성공적으로 생성되었습니다.", HttpStatus.CREATED.value()),
	RUNNING_RECORD_CREATE_FAIL("RUNNING_RECORD_CREATE_FAIL",
		"러닝 기록 생성이 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
	RUNNING_RECORD_GET_SUCCESS("RUNNING_RECORD_GET_SUCCESS",
		"러닝 기록을 성공적으로 가져왔습니다.", HttpStatus.OK.value()),
	RUNNING_RECORD_GET_FAIL("RUNNING_RECORD_GET_FAIL",
		"러닝 기록 가져오기가 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());

	private final String code;
	private final String message;
	private final int status;

	RunningRecordServiceCode(String code, String message, int status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}
}
