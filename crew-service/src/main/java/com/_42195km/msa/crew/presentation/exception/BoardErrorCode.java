package com._42195km.msa.crew.presentation.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.exception.code.ErrorCode;

import lombok.Getter;

@Getter
public enum BoardErrorCode implements ErrorCode {
	CREW_BOARD_CREATE_POST_SUCCESS("CREW_020", "게시글 생성 성공", HttpStatus.OK),
	CREW_BOARD_CREATE_POST_FAILED("CREW_021", "게시글 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_UPDATE_POST_SUCCESS("CREW_022", "게시글 수정 성공", HttpStatus.OK),
	CREW_BOARD_UPDATE_POST_FAILED("CREW_023", "게시글 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_GET_POST_SUCCESS("CREW_024", "게시글 조회 성공", HttpStatus.OK),
	CREW_BOARD_GET_POST_FAILED("CREW_025", "게시글 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_SEARCH_POST_SUCCESS("CREW_026", "게시글 검색 성공", HttpStatus.OK),
	CREW_BOARD_SEARCH_POST_FAILED("CREW_027", "게시글 검색 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_DELETE_POST_SUCCESS("CREW_028", "게시글 삭제 성공", HttpStatus.OK),
	CREW_BOARD_DELETE_POST_FAILED("CREW_029", "게시글 삭제 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_CREATE_COMMENT_SUCCESS("CREW_030", "댓글 생성 성공", HttpStatus.OK),
	CREW_BOARD_CREATE_COMMENT_FAILED("CREW_031", "댓글 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_GET_COMMENT_FAILED("CREW_032", "댓글 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	CREW_BOARD_UPDATE_COMMENT_SUCCESS("CREW_033", "댓글 수정 성공", HttpStatus.OK),
	CREW_BOARD_UPDATE_COMMENT_FAILED("CREW_034", "댓글 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR),
	;

	private final String code;
	private final String message;
	private final int status;

	BoardErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status.value();
	}
}
