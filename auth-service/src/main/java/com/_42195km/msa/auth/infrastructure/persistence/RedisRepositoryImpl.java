package com._42195km.msa.auth.infrastructure.persistence;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RedisRepositoryImpl implements RedisRepository {

	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${JWT_REFRESH_EXPIRATION}")
	private long refreshTokenExpiration;

	@Override
	public void saveRefreshToken(String key, String refreshToken, long creationTokenTime) {

		// Refresh 토큰 생성 시점과 실제 레디스에 그 토큰이 저장되는 시점이 다르므로 계산 필요
		/*
			Ex) 사용자가 로그인 하여 Access 토큰과 Refresh 토큰이 생성된 시점 : 2025/4/10일 9시 00분 00초 000ms
				실제 레디스에 Refresh 토큰이 저장될때 시점 : 2025/4/10일 9시 00분 00초 100ms
				100ms 만큼 차이가 발생하므로 TTL에 그 차이만큼 빼서 저장
		 */
		long saveTime = System.currentTimeMillis();

		long ttl = refreshTokenExpiration - (saveTime - creationTokenTime);

		redisTemplate.opsForValue().set(
			"Refresh:" + key,
			refreshToken,
			ttl,
			java.util.concurrent.TimeUnit.MILLISECONDS
		);

		// TTL 확인
		Long remainingTTL = redisTemplate.getExpire("Refresh:" + key, java.util.concurrent.TimeUnit.MILLISECONDS);

		if (remainingTTL != null) {
			System.out.println(remainingTTL + " milliseconds.");
			log.info("Expire Refresh Token: {}", remainingTTL);
		} else {
			log.info("No Refresh Token");
		}

	}

	@Override
	public void blackListToken(String accessToken, long ttl) {

		String tokenHash = DigestUtils.sha256Hex(accessToken);

		redisTemplate.opsForValue().set(
			"BlackList:" + tokenHash,
			accessToken,
			ttl,
			java.util.concurrent.TimeUnit.MILLISECONDS);
	}

}
