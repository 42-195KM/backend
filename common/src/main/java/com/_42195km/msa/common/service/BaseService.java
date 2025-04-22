package com._42195km.msa.common.service;

import java.util.function.Supplier;

import com._42195km.msa.common.code.ServiceCode;
import com._42195km.msa.common.exception.CustomBusinessException;

public abstract class BaseService {
	protected <T> T execute(Supplier<T> action, ServiceCode code) {
		try {
			return action.get();
		} catch (Exception e){
			throw CustomBusinessException.from(code);
		}
	}
}
