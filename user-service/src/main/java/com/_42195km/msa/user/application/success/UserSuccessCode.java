package com._42195km.msa.user.application.success;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode {

	CREATE_USER_SUCCESS(HttpStatus.CREATED, "유저가 성공적으로 생성되었습니다.", "S_USER_CREATED"),
	FIND_ALL_USER_SUCCESS(HttpStatus.OK, "모든 유저가 성공적으로 조회되었습니다.", "S_USER_FIND_ALL"),
	FIND_ONE_USER_SUCCESS(HttpStatus.OK, "단건 유저가 성공적으로 조회되었습니다.", "S_USER_FIND_ONE"),
	FIND_KEYWORD_USER_LIST_SUCCESS(HttpStatus.OK, "키워드 검색이 성공적으로 조회되었습니다.", "S_USER_FIND_KEYWORD"),
	UPDATE_USER_SUCCESS(HttpStatus.OK, "유저 정보 업데이트가 성공적으로 완료되었습니다.", "S_USER_UPDATE"),
	DELETE_USER_SUCCESS(HttpStatus.NO_CONTENT, "회원 탈퇴가 성공적으로 완료되었습니다.", "S_USER_DELETE_SUCCESS"),
	BAN_USER_SUCCESS(HttpStatus.NO_CONTENT, "회원을 성공적으로 밴 했습니다.", "S_USER_BAN_USER_SUCCESS"),
	;

	private final HttpStatus statusCode;
	private final String message;
	private final String code;
}
