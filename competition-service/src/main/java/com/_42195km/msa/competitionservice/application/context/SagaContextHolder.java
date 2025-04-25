package com._42195km.msa.competitionservice.application.context;

import org.springframework.stereotype.Component;

@Component
public class SagaContextHolder {
	private static final ThreadLocal<String> CURRENT_SAGA_ID = new ThreadLocal<>();

	/**
	 * 현재 스레드의 Saga ID 설정
	 * @param sagaId 설정할 Saga ID
	 */
	public static void setCurrentSagaId(String sagaId) {
		CURRENT_SAGA_ID.set(sagaId);
	}

	/**
	 * 현재 스레드의 Saga ID 조회
	 * @return 현재 Saga ID, 없으면 null
	 */
	public static String getCurrentSagaId() {
		return CURRENT_SAGA_ID.get();
	}

	/**
	 * 현재 스레드의 Saga 컨텍스트 정보 제거
	 */
	public static void clear() {
		CURRENT_SAGA_ID.remove();
	}
}
