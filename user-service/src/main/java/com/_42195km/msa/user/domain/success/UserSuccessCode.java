package com._42195km.msa.user.domain.success;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode {

	CREATE_USER_SUCCESS(HttpStatus.CREATED, "유저가 성공적으로 생성되었습니다.", "S_USER_CREATED"),
	FIND_ALL_USER_SUCCESS(HttpStatus.OK, "모든 유저가 성공적으로 조회되었습니다.", "S_USER_FIND_ALL"),
	FIND_ONE_USER_SUCCESS(HttpStatus.OK, "단건 유저가 성공적으로 조회되었습니다.", "S_USER_FIND_ONE"),
	;

	private final HttpStatus statusCode;
	private final String message;
	private final String code;
}
