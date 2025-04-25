package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SagaStateRepository {
	private final RedisTemplate<String, SagaState> sagaRedisTemplate;
	private final RedisTemplate<String, String> sagaStringRedisTemplate;
	private static final String SAGA_KEY_PREFIX = "saga:";
	private static final long SAGA_TTL_HOURS = 24;

	public SagaStateRepository(
		RedisTemplate<String, SagaState> sagaRedisTemplate,
		@Qualifier("sagaStringRedisTemplate") RedisTemplate<String, String> stringRedisTemplate) {
		this.sagaRedisTemplate = sagaRedisTemplate;
		this.sagaStringRedisTemplate = stringRedisTemplate;
	}

	public void saveSagaState(SagaState state) {
		String key = SAGA_KEY_PREFIX + state.getSagaId();
		try {
			// 메인 Saga 상태 저장
			sagaRedisTemplate.opsForValue().set(key, state);
			sagaRedisTemplate.expire(key, SAGA_TTL_HOURS, TimeUnit.HOURS);

			// 활성 상태인 경우 인덱스 저장 (문자열 템플릿 사용)
			if (state.getStatus() == SagaStatus.STARTED || state.getStatus() == SagaStatus.IN_PROGRESS) {
				String indexKey = "sagaIndex:" + state.getCompetitionId() + ":" + state.getParticipantId();
				sagaStringRedisTemplate.opsForValue().set(indexKey, state.getSagaId());
				sagaStringRedisTemplate.expire(indexKey, SAGA_TTL_HOURS, TimeUnit.HOURS);
			}

			log.info("Saved saga state with ID: {}", state.getSagaId());
		} catch (Exception e) {
			log.error("Error saving saga state: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to save saga state", e);
		}
	}

	public SagaState getSagaState(String sagaId) {
		String key = SAGA_KEY_PREFIX + sagaId;
		try {
			SagaState state = sagaRedisTemplate.opsForValue().get(key);
			if (state == null) {
				log.warn("No saga state found for ID: {}", sagaId);
			}
			return state;
		} catch (Exception e) {
			log.error("Error retrieving saga state: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to retrieve saga state", e);
		}
	}

	public void deleteSagaState(String sagaId) {
		String key = SAGA_KEY_PREFIX + sagaId;
		try {
			sagaRedisTemplate.delete(key);
			log.info("Deleted saga state with ID: {}", sagaId);
		} catch (Exception e) {
			log.error("Error deleting saga state: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to delete saga state", e);
		}
	}

	/**
	 * 활성 Saga ID 조회
	 * @param competitionId
	 * @param participantId
	 * @return
	 */
	public String findActiveSagaId(UUID competitionId, UUID participantId) {
		String lookupKey = "sagaIndex:" + competitionId + ":" + participantId;
		String sagaId = sagaStringRedisTemplate.opsForValue().get(lookupKey);

		if (sagaId != null) {
			// Saga 상태 유효성 검증
			SagaState state = getSagaState(sagaId);
			if (state != null &&
				(state.getStatus() == SagaStatus.STARTED || state.getStatus() == SagaStatus.IN_PROGRESS)) {
				return sagaId;
			}

			// 인덱스는 있지만 상태가 활성이 아니거나 없는 경우, 인덱스 제거
			sagaStringRedisTemplate.delete(lookupKey);
		}

		return null;
	}

}
