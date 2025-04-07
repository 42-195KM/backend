package com._42195km.msa.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
  METHOD_ARGUMENT_NOT_VALID("COM_001", "Validation 실패", HttpStatus.BAD_REQUEST),

  // board
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



  ;

  private final String code;
  private final String message;
  private final int status;

  CommonErrorCode(String code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status.value();
  }
}
