package com._42195km.msa.user.infrastructure.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = OptionalNotBlankValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalNotBlank {
	String message() default "값이 있다면 공백일 수 없습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
