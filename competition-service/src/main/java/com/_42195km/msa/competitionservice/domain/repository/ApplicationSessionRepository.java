package com._42195km.msa.competitionservice.domain.repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com._42195km.msa.competitionservice.domain.model.ApplicationSession;

@Repository
public class ApplicationSessionRepository {
	private static final String SESSION_KEY_PREFIX = "app_session:";
	private static final long SESSION_TTL_HOURS = 24;

	private final RedisTemplate<String, ApplicationSession> redisTemplate;

	public ApplicationSessionRepository(RedisTemplate<String, ApplicationSession> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void saveSession(ApplicationSession session) {
		String key = SESSION_KEY_PREFIX + session.getSessionId();
		redisTemplate.opsForValue().set(key, session);
		redisTemplate.expire(key, SESSION_TTL_HOURS, TimeUnit.HOURS);
	}

	public ApplicationSession getSession(String sessionId) {
		String key = SESSION_KEY_PREFIX + sessionId;
		return redisTemplate.opsForValue().get(key);
	}

	public ApplicationSession findByCompetitionAndParticipant(UUID competitionId, UUID participantId) {
		// TODO : 보조 인덱스나 검색을 사용 구현
		return null;
	}
}
