package com._42195km.msa.crew.application.exception;

import com._42195km.msa.common.code.ServiceCode;
import com._42195km.msa.common.exception.CustomBusinessException;

public class CrewBusinessException extends CustomBusinessException {

	protected CrewBusinessException(ServiceCode code) {
		super(code);
	}
}
