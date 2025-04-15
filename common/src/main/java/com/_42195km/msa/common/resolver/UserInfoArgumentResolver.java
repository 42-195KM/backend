package com._42195km.msa.common.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserInfoArgumentResolver implements HandlerMethodArgumentResolver {
	private static final String USER_ID_HEADER = "X-User-Id";
	private static final String USER_ROLE_HEADER = "X-User-Role";

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(UserInfo.class)
			&& parameter.getParameterType().equals(UserInfoDto.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();

		String userId = request.getHeader(USER_ID_HEADER);
		String role = request.getHeader(USER_ROLE_HEADER);

		if (userId == null && role == null) {
			return UserInfoDto.empty();
		}

		return UserInfoDto.of(userId, role);

	}
}
