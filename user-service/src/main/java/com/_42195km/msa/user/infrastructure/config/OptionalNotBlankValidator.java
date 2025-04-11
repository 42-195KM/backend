package com._42195km.msa.user.infrastructure.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class OptionalNotBlankValidator implements ConstraintValidator<OptionalNotBlank, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// null은 허용, 공백은 금지
		return value == null || !value.isBlank();
	}
}
