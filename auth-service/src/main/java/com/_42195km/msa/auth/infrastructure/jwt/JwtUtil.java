package com._42195km.msa.auth.infrastructure.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com._42195km.msa.auth.domain.model.UserRole;
import com._42195km.msa.auth.infrastructure.jwt.exception.JwtException;
import com._42195km.msa.common.exception.CustomBusinessException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {

	private static final String BEARER_PREFIX = "Bearer ";

	private final Key key;
	private final long accessExpiration;
	private final long refreshExpiration;

	public JwtUtil(
		@Value("${service.jwt.secret-key}") String secretKey,
		@Value("${service.jwt.access-expiration}") long accessExpiration,
		@Value("${service.jwt.refresh-expiration}") long refreshExpiration
	) {
		byte[] decodedKey = Base64.getDecoder().decode(secretKey);
		this.key = Keys.hmacShaKeyFor(decodedKey);
		this.accessExpiration = accessExpiration;
		this.refreshExpiration = refreshExpiration;
	}

	// Access-Token 생성
	public String createAccessToken(UUID userId, String userName, UserRole userRole) {

		// 토큰에 들어갈 정보
		String token = Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("userId", userId)
			.claim("userName", userName)
			.claim("role", userRole)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();

		// 토큰에 Prefix 더해서 리턴
		return BEARER_PREFIX + token;
	}

	// Refresh-Token 생성
	public String createRefreshToken(UUID userId) {

		return Jwts.builder()
			.setSubject(String.valueOf(userId))
			.claim("role", "REFRESH")
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// 기본 토큰 검증
	public void validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(removePrefix(token));
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			throw CustomBusinessException.from(JwtException.INVALID_JWT_SIGNATURE);
		} catch (ExpiredJwtException e) {
			throw CustomBusinessException.from(JwtException.EXPIRED_JWT_TOKEN);
		} catch (UnsupportedJwtException e) {
			throw CustomBusinessException.from(JwtException.UNSUPPORTED_JWT_TOKEN);
		} catch (IllegalArgumentException e) {
			throw CustomBusinessException.from(JwtException.JWT_CLAIM_IS_EMPTY);
		}
	}

	// 액세스 토큰 검증 (만료 예외 제외)
	public void validateAccessToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(removePrefix(token));
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			throw CustomBusinessException.from(JwtException.INVALID_JWT_SIGNATURE);
		} catch (UnsupportedJwtException e) {
			throw CustomBusinessException.from(JwtException.UNSUPPORTED_JWT_TOKEN);
		} catch (IllegalArgumentException e) {
			throw CustomBusinessException.from(JwtException.JWT_CLAIM_IS_EMPTY);
		}
	}

	// Claims 뜯어오기
	public Claims parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(removePrefix(token))
			.getBody();
	}

	// Prefix 제거
	public String removePrefix(String token) {
		if (token != null && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		}

		return token;
	}

}
