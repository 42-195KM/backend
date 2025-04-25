package com._42195km.msa.competitionservice.infrastructure.messaging;

import com._42195km.msa.competitionservice.application.event.SagaEvent;

public interface EventPublisher {

	/**
	 * Saga 이벤트를 발행합니다.
	 *
	 * @param topic 이벤트를 발행할 토픽 이름
	 * @param key 이벤트의 키 (partitioning 목적)
	 * @param event 발행할 이벤트 객체
	 * @param <T> SagaEvent 타입 또는 하위 타입
	 */
	<T extends SagaEvent> void publishSagaEvent(String topic, String key, T event);

	/**
	 * 특정 Saga에 대한 보상 이벤트를 발행합니다.
	 *
	 * @param topic 이벤트를 발행할 토픽 이름
	 * @param sagaId Saga ID
	 * @param originalEvent 원본 이벤트
	 */
	void publishCompensationEvent(String topic, String sagaId, SagaEvent originalEvent);

	/**
	 * 알림 이벤트를 발행합니다.
	 *
	 * @param topic 이벤트를 발행할 토픽 이름
	 * @param key 이벤트의 키
	 * @param notification 알림 데이터
	 * @param <T> 알림 데이터 타입
	 */
	<T> void publishNotificationEvent(String topic, String key, T notification);
}
