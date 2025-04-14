package com._42195km.msa.common.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com._42195km.msa.common.exception.CustomBusinessException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class PermissionAspect {

	@Before("@annotation(checkPermission)")
	public void checkPermission(JoinPoint joinPoint, CheckPermission checkPermission) {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		String roleHeader = request.getHeader("X-User-Role");
		if (roleHeader == null || roleHeader.isEmpty()) {
			throw CustomBusinessException.from(PermissionException.ACCESS_DENIED);
		}

		// "MASTER", "COMPANY", "NORMAL"
		String userRole = roleHeader.trim().toUpperCase();

		String[] requiredRoles = checkPermission.roles();
		CheckPermission.Mode mode = checkPermission.mode();

		boolean hasPermission;
		if (mode == CheckPermission.Mode.ALL) {
			hasPermission = Arrays.stream(requiredRoles)
				.allMatch(userRole::equalsIgnoreCase);
		} else {
			hasPermission = Arrays.stream(requiredRoles)
				.anyMatch(userRole::equalsIgnoreCase);
		}

		if (!hasPermission) {
			throw CustomBusinessException.from(PermissionException.DIFFERNT_PERMISSION);
		}
	}
}
