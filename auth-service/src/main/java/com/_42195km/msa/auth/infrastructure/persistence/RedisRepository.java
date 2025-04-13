package com._42195km.msa.auth.infrastructure.persistence;

public interface RedisRepository {

	void saveRefreshToken(String string, String refreshToken, long creationTokenTime);

}
