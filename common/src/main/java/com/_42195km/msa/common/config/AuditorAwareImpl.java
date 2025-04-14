package com._42195km.msa.common.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuditorAwareImpl implements AuditorAware<UUID> {

	@Override
	public Optional<UUID> getCurrentAuditor() {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

		log.info(">>> X-User-Id: {}", attributes.getRequest().getHeader("X-User-Id"));

		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			String userId = request.getHeader("X-User-Id");

			if (userId != null) {
				try {
					return Optional.of(UUID.fromString(userId));
				} catch (IllegalArgumentException e) {
					return Optional.empty();
				}
			}
		}
		return Optional.empty();
	}
}
