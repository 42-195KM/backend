package com._42195km.msa.common.exception;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;

@Getter
public class CustomBusinessException extends RuntimeException {

	private final ServiceCode code;

	protected CustomBusinessException(ServiceCode code) {
		super(code.getMessage());
		this.code = code;
	}

	public static CustomBusinessException from(ServiceCode code) {
		return new CustomBusinessException(code);
	}

}
