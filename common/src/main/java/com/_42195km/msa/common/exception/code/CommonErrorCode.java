package com._42195km.msa.common.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {
  METHOD_ARGUMENT_NOT_VALID("COM_001", "Validation 실패", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final int status;

  CommonErrorCode(String code, String message, HttpStatus status) {
    this.code = code;
    this.message = message;
    this.status = status.value();
  }
}
