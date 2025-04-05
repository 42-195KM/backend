package com._42195km.msa.common.exception;

import com._42195km.msa.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class CustomBusinessException extends RuntimeException {

  private final ErrorCode code;

  protected CustomBusinessException(ErrorCode code) {
    super(code.getMessage());
    this.code = code;
  }

  public static CustomBusinessException from(ErrorCode code) {
    return new CustomBusinessException(code);
  }

}
