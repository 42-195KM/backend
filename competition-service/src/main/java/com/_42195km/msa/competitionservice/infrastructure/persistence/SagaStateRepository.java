package com._42195km.msa.competitionservice.infrastructure.persistence;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.SagaState;
import com._42195km.msa.competitionservice.domain.model.SagaStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SagaStateRepository {
	private final RedisTemplate<String, SagaState> sagaRedisTemplate;
	private static final String SAGA_KEY_PREFIX = "saga:";
	private static final long SAGA_TTL_HOURS = 24;

	public void saveSagaState(SagaState state) {
		String key = SAGA_KEY_PREFIX + state.getSagaId();
		try {
			sagaRedisTemplate.opsForValue().set(key, state);
			sagaRedisTemplate.expire(key, SAGA_TTL_HOURS, TimeUnit.HOURS);
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
		// Redis에서 일치하는 모든 키 조회
		Set<String> keys = sagaRedisTemplate.keys(SAGA_KEY_PREFIX + "*");

		if (keys == null || keys.isEmpty()) {
			return null;
		}

		// 각 키에 대해 SagaState 조회 및 비교
		for (String key : keys) {
			SagaState state = sagaRedisTemplate.opsForValue().get(key);
			if (state != null &&
				competitionId.equals(state.getCompetitionId()) &&
				participantId.equals(state.getParticipantId()) &&
				(state.getStatus() == SagaStatus.STARTED || state.getStatus() == SagaStatus.IN_PROGRESS)) {
				return state.getSagaId();
			}
		}

		return null;
	}

}
