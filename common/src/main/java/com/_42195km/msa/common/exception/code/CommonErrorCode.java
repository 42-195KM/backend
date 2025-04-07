package com._42195km.msa.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
  METHOD_ARGUMENT_NOT_VALID("COM_001", "Validation 실패", HttpStatus.BAD_REQUEST),

  // board
  CREW_BOARD_CREATE_POST_SUCCESS("CREW_020", "게시글 생성 성공", HttpStatus.OK),
  CREW_BOARD_CREATE_POST_FAILED("CREW_021", "게시글 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
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
