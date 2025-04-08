package com._42195km.msa.competitionservice.presentation;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.exception.code.ErrorCode;

import lombok.Getter;

@Getter
public enum CompetitionErrorCode implements ErrorCode {

	METHOD_ARGUMENT_NOT_VALID("COM_001", "Validation 실패", HttpStatus.BAD_REQUEST);

	private final String code;
	private final String message;
	private final int status;

	CompetitionErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status.value();
	}
}
