package com._42195km.msa.competitionservice.application.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public enum CompetitionServiceCode implements ServiceCode {

	COMPETITION_CREATE_SUCCESS("CPT_001", "대회 생성 성공", HttpStatus.OK),
	COMPETITION_CREATE_FAIL("CPT_002", "대회 생성 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_GET_SUCCESS("CPT_003", "대회 조회 성공", HttpStatus.OK),
	COMPETITION_GET_FAIL("CPT_004", "대회 조회 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_GET_ID_FAIL("CPT_005", "해당 ID의 대회가 없습니다.", HttpStatus.BAD_REQUEST),
	COMPETITION_SEARCH_SUCCESS("CPT_006", "대회 검색 성공", HttpStatus.OK),
	COMPETITION_SEARCH_FAIL("CPT_007", "대회 검색 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_UPDATE_SUCCESS("CPT_008", "대회 수정 성공", HttpStatus.OK),
	COMPETITION_UPDATE_FAIL("CPT_009", "대회 수정 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_DELETE_SUCCESS("CPT_010", "대회 삭제 성공", HttpStatus.OK),
	COMPETITION_DELETE_FAIL("CPT_011", "대회 삭제 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_APPLY_SUCCESS("CPT_012", "대회 신청 성공", HttpStatus.OK),
	COMPETITION_APPLY_FAIL("CPT_013", "대회 신청 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_APPLY_EXIST("CPT_014", "대회 중복 신청", HttpStatus.BAD_REQUEST),
	COMPETITION_APPLY_FIRST_FAIL("CPT_015", "대회 선착순 마감", HttpStatus.BAD_REQUEST),
	COMPETITION_DRAW_INVALID_TYPE("CPT_016", "대회 접수 유형 오류", HttpStatus.BAD_REQUEST),
	COMPETITION_DRAW_FAIL("CPT_017", "대회 추첨 실패", HttpStatus.BAD_REQUEST),
	COMPETITION_DRAW_SUCCESS("CPT_018", "대회 추첨 성공", HttpStatus.OK),
	PARTICIPANT_GET_FAIL("CPT_019", "대회 참가자 조회 실패", HttpStatus.BAD_REQUEST),
	PARTICIPANT_GET_SUCCESS("CPT_020", "대회 참가자 조회 성공", HttpStatus.OK),
	PARTICIPANT_SEARCH_FAIL("CPT_021", "대회 참가자 검색 실패", HttpStatus.BAD_REQUEST),
	PARTICIPANT_SEARCH_SUCCESS("CPT_022", "대회 참가자 검색 성공", HttpStatus.OK),
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
