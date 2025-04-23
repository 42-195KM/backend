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

	private static final ThreadLocal<UUID> currentAuditor = new ThreadLocal<>();

	@Override
	public Optional<UUID> getCurrentAuditor() {

		// ThreadLocal에서 사용자 UUID를 가져옴
		UUID userId = currentAuditor.get();
		if (userId != null) {
			return Optional.of(userId);
		}

		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attributes != null) {
			HttpServletRequest request = attributes.getRequest();
			String userIdFromHeader = request.getHeader("X-User-Id");

			if (userIdFromHeader != null) {
				try {
					UUID userUUID = UUID.fromString(userIdFromHeader);
					// ThreadLocal에 X-User-Id 값을 설정
					setCurrentAuditor(userUUID);
					return Optional.of(userUUID);
				} catch (IllegalArgumentException e) {
					return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
				}
			}
		}

		// 서버측 고정 UUID 반환
		return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
	}

	// 요청 범위 내에서 X-User-Id를 설정할 메소드
	public static void setCurrentAuditor(UUID userId) {
		currentAuditor.set(userId);
	}

	// 스레드 재사용 방지
	public static void clear() {
		currentAuditor.remove();  // ⭐ 중요
	}

}
