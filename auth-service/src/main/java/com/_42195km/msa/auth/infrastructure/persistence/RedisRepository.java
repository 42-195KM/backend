package com._42195km.msa.auth.infrastructure.persistence;

import java.util.UUID;

public interface RedisRepository {

	void saveRefreshToken(String string, String refreshToken, long creationTokenTime);

	void blackListToken(String accessToken, long ttl);

	boolean isBlackListedToken(String accessToken);

	boolean isRefreshToken(UUID userId);

	void deleteRefreshToken(UUID userId);
}
