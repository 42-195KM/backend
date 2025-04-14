package com._42195km.msa.common.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPermission {
	String[] roles();

	Mode mode() default Mode.ALL;

	// ALL -> 모든 권한이 있어야 접근 가능
	// ANY -> 지정된 권한 중 하나만 있어도 접근 가능
	enum Mode {
		ALL,
		ANY
	}
}