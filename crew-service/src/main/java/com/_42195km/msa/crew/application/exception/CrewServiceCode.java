package com._42195km.msa.crew.application.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum CrewServiceCode implements ServiceCode {
	CREW_CREATE_POST_SUCCESS("CREW_001", "크루 생성 성공", HttpStatus.OK),
	CREW_NAME_DUPLICATED("CREW_002", "크루 이름은 중복될 수 없습니다", HttpStatus.BAD_REQUEST),
	CREW_APPLY_JOIN_POST_SUCCESS("CREW_003", "크루 가입 신청 성공", HttpStatus.OK),
	CREW_NOT_FOUND("CREW_004", "크루를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
	CREW_IS_FULL("CREW_005", "이 크루는 이미 인원이 최대입니다", HttpStatus.BAD_REQUEST),
	CREW_MEMBER_ALREADY_JOINED("CREW_006", "이미 이 크루에 가입된 사용자입니다", HttpStatus.BAD_REQUEST),
	CREW_SEARCH_GET_SUCCESS("CREW_007", "크루 검색 성공", HttpStatus.OK),
	CREW_SPECIFIC_GET_SUCCESS("CREW_008", "크루 상세 조회 성공", HttpStatus.OK),
	CREW_UPDATE_PATCH_SUCCESS("CREW_009", "크루 정보 수정 성공", HttpStatus.OK),
	CREW_DELETE_DELETE_SUCCESS("CREW_010","크루 정보 삭제 성공" , HttpStatus.NO_CONTENT),
	CREW_AGREE_JOIN_PATCH_SUCCESS("CREW_011","크루장이 가입 신청을 승인하였습니다", HttpStatus.OK),
	CREW_REJECT_JOIN_PATCH_SUCCESS("CREW_012","크루장이 가입 신청을 거절하였습니다", HttpStatus.OK);

	private final String code;
	private final String message;
	private final int status;

	CrewServiceCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status.value();
	}
}

