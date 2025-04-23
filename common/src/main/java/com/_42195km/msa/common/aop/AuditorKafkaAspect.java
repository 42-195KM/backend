package com._42195km.msa.common.aop;

import java.util.Map;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com._42195km.msa.common.config.AuditorAwareImpl;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class AuditorKafkaAspect {

	@Around("@annotation(com._42195km.msa.common.aop.AuditingKafkaListener)")
	public Object injectAuditorId(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			for (Object arg : joinPoint.getArgs()) {
				if (arg instanceof Map<?, ?> map) {
					Object userId = map.get("userId");
					if (userId != null) {
						UUID userUUID = UUID.fromString(userId.toString());
						log.info(">>> Map의 userId : {}", userUUID);
						AuditorAwareImpl.setCurrentAuditor(userUUID);
					} else {
						log.warn(">>> userId를 찾지 못함");
					}
				}
			}

			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
