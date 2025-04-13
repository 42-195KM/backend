package com._42195km.msa.auth.application.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.auth.application.dto.request.RefreshTokenRequestDto;
import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
import com._42195km.msa.auth.application.dto.response.AccessTokenReissueResponseDto;
import com._42195km.msa.auth.application.dto.response.UserLogInResponseDto;
import com._42195km.msa.auth.application.exception.AuthException;
import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;
import com._42195km.msa.auth.infrastructure.jwt.JwtUtil;
import com._42195km.msa.auth.infrastructure.persistence.AuthRepositoryImpl;
import com._42195km.msa.auth.infrastructure.persistence.RedisRepositoryImpl;
import com._42195km.msa.common.exception.CustomBusinessException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtUtil jwtUtil;
	private final AuthRepositoryImpl authRepositoryImpl;
	private final RedisRepositoryImpl redisRepositoryImpl;
	private final PasswordEncoder passwordEncoder;
	private final HttpServletRequest request;

	@Override
	// @Transactional
	public UserLogInResponseDto logIn(UserLogInRequestDto userLogInRequestDto) {

		Auth auth = authRepositoryImpl.findByUserName(userLogInRequestDto.getUsername())
			.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

		if (!validatePassword(userLogInRequestDto.getPassword(), auth.getPassword())) {
			throw CustomBusinessException.from(AuthException.LOGIN_FAILED_WORNG_PASSWORD);
		}

		UUID userId = auth.getUserUuid();
		String userName = auth.getUsername();
		UserRole role = auth.getRole();

		String accessToken = jwtUtil.createAccessToken(userId, userName, role);
		String refreshToken = jwtUtil.createRefreshToken(userId);

		// 토큰이 생성된 시점
		long creationTokenTime = System.currentTimeMillis();

		UserLogInResponseDto userLogInResponseDto = UserLogInResponseDto.
			builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();

		if (userLogInResponseDto != null) {
			redisRepositoryImpl.saveRefreshToken(userId.toString(), refreshToken, creationTokenTime);
		} else {
			throw CustomBusinessException.from(AuthException.FAILED_SAVE_REFRESHTOKEN);
		}

		return userLogInResponseDto;
	}

	@Override
	public AccessTokenReissueResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {

		// 토큰 유효성 검증
		String refreshToken = refreshTokenRequestDto.getRefreshToken();
		jwtUtil.validateToken(refreshToken);

		// 헤더에 현재 토큰 뜯어와서 블랙리스트 처리
		String accessToken = extractAccessTokenFromHeader(request);
		jwtUtil.validateToken(accessToken);

		// 현재 헤더의 토큰에 블랙리스트가 있는지 확인
		if (redisRepositoryImpl.isBlackListedToken(accessToken)) {
			throw CustomBusinessException.from(AuthException.ACCESS_TOKEN_BLACKLISTED);
		}

		if (accessToken != null) {
			redisRepositoryImpl.blackListToken(accessToken, calculateExpires(accessToken));
		}

		// 리프레쉬 토큰에서 UserUuId 조회해서 다시 토큰 생성
		UUID userUuId = UUID.fromString(jwtUtil.parseClaims(refreshToken).getSubject());
		Auth auth = authRepositoryImpl.findByUserUuid(userUuId)
			.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

		UUID userId = auth.getUserUuid();
		String userName = auth.getUsername();
		UserRole role = auth.getRole();

		String reissuedAccessToken = jwtUtil.createAccessToken(userId, userName, role);

		AccessTokenReissueResponseDto accessTokenReissueResponseDto
			= AccessTokenReissueResponseDto.builder()
			.accessToken(reissuedAccessToken)
			.build();

		return accessTokenReissueResponseDto;
	}

	private long calculateExpires(String accessToken) {
		long now = System.currentTimeMillis();
		long expires = jwtUtil.parseClaims(accessToken).getExpiration().getTime();

		log.info("time: {}", expires - now);

		return expires - now;
	}

	private String extractAccessTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		return jwtUtil.removePrefix(authHeader);
	}

	private boolean validatePassword(String requestPassword, String authDbPassword) {
		return passwordEncoder.matches(requestPassword, authDbPassword);
	}
}
