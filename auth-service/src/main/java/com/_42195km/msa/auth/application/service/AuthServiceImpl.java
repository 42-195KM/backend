package com._42195km.msa.auth.application.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.auth.application.dto.request.BlackListRequestDto;
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
import com._42195km.msa.auth.presentation.dto.request.CreateAuthRequestDto;
import com._42195km.msa.auth.presentation.dto.request.TokenRequestDto;
import com._42195km.msa.auth.presentation.dto.request.UpdateAuthRequestDto;
import com._42195km.msa.auth.presentation.dto.response.CreateAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.UpdateAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.ValidateTokenResponse;
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

		// 리프레시 토큰 저장
		long creationTokenTime = System.currentTimeMillis();
		redisRepositoryImpl.saveRefreshToken(userId.toString(), refreshToken, creationTokenTime);

		return UserLogInResponseDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Override
	public AccessTokenReissueResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {

		// 리프레시 토큰 유효성 검증
		String refreshToken = refreshTokenRequestDto.getRefreshToken();
		jwtUtil.validateToken(refreshToken);

		String accessToken = extractAccessTokenFromHeader(request);

		// 액세스 토큰이 있으면 블랙리스트 체크 및 블랙리스트 처리
		if (accessToken != null) {
			handleAccessTokenBlackList(accessToken);
		}

		// 리프레시 토큰을 이용해 새 액세스 토큰 발급
		return issueNewAccessTokenWithRefreshToken(refreshToken);
	}

	@Override
	public void logOut(UUID userId) {

		// 리프레시 토큰 삭제
		if (!redisRepositoryImpl.isRefreshToken(userId)) {
			throw CustomBusinessException.from(AuthException.NO_LOGIN_USER);
		}
		redisRepositoryImpl.deleteRefreshToken(userId);

		// 액세스 토큰을 블랙리스트에 추가
		String accessToken = extractAccessTokenFromHeader(request);
		if (accessToken != null) {
			handleAccessTokenBlackList(accessToken);
		}

	}

	@Override
	public void blackList(BlackListRequestDto blackListRequestDto) {

		String accessToken = blackListRequestDto.getAccessToken();
		jwtUtil.validateToken(accessToken);
		handleAccessTokenBlackList(accessToken);
	}

	@Override
	public ValidateTokenResponse validateToken(TokenRequestDto tokenRequestDto) {

		String token = tokenRequestDto.getToken();
		if (redisRepositoryImpl.isBlackListedToken(token)) {
			throw CustomBusinessException.from(AuthException.ACCESS_TOKEN_BLACKLISTED);
		}

		UUID userId = UUID.fromString(jwtUtil.parseClaims(token).getSubject());
		Auth auth = authRepositoryImpl.findByUserUuid(userId)
			.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

		ValidateTokenResponse validateTokenResponse = ValidateTokenResponse.builder()
			.userId(auth.getUserUuid())
			.userName(auth.getUsername())
			.role(auth.getRole())
			.build();

		return validateTokenResponse;
	}

	@Override
	@Transactional
	public CreateAuthResponseDto createAuth(CreateAuthRequestDto createAuthRequestDto) {

		Auth auth = CreateAuthRequestDto.toAuth(createAuthRequestDto);

		Auth savedAuth = authRepositoryImpl.save(auth);

		return CreateAuthResponseDto.from(savedAuth);
	}

	@Override
	@Transactional
	public UpdateAuthResponseDto updateAuth(UpdateAuthRequestDto updateAuthRequestDto) {

		Auth auth = authRepositoryImpl.findByUserUuid(updateAuthRequestDto.getUserId())
			.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

		auth.update(updateAuthRequestDto);

		return UpdateAuthResponseDto.from(auth);
	}

	@Override
	@Transactional
	public void deleteAuth(UUID userId) {

		Auth auth = authRepositoryImpl.findByUserUuid(userId)
			.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

		auth.setDeleted();
	}

	/// 액세스 토큰 블랙리스트 처리 메서드
	private void handleAccessTokenBlackList(String accessToken) {

		// 토큰 유효성 검증
		jwtUtil.validateAccessToken(accessToken);

		// 블랙리스트에 있는지 확인
		if (redisRepositoryImpl.isBlackListedToken(accessToken)) {
			throw CustomBusinessException.from(AuthException.ACCESS_TOKEN_BLACKLISTED);
		}

		// 블랙리스트에 없으면 블랙리스트 처리
		redisRepositoryImpl.blackListToken(accessToken, calculateExpires(accessToken));
	}

	/// 리프레시 토큰으로 새로운 액세스 토큰을 발급하는 메서드
	private AccessTokenReissueResponseDto issueNewAccessTokenWithRefreshToken(String refreshToken) {

		// 리프레시 토큰에서 사용자 정보 추출 -> Auth DB에 저장된 정보로 새 토큰 발행
		UUID userUuId = UUID.fromString(jwtUtil.parseClaims(refreshToken).getSubject());
		Auth auth = authRepositoryImpl.findByUserUuid(userUuId)
			.orElseThrow(() -> CustomBusinessException.from(AuthException.NOT_FOUND_AUTH_USER));

		UUID userId = auth.getUserUuid();
		String userName = auth.getUsername();
		UserRole role = auth.getRole();

		String reissuedAccessToken = jwtUtil.createAccessToken(userId, userName, role);

		return AccessTokenReissueResponseDto.builder()
			.accessToken(reissuedAccessToken)
			.build();
	}

	private long calculateExpires(String accessToken) {
		long now = System.currentTimeMillis();
		long expires = jwtUtil.parseClaims(accessToken).getExpiration().getTime();

		log.info("time: {}", expires - now);

		return expires - now;
	}

	private String extractAccessTokenFromHeader(HttpServletRequest request) {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || authHeader.isEmpty()) {
			return null;
		}

		return jwtUtil.removePrefix(authHeader);
	}

	private boolean validatePassword(String requestPassword, String authDbPassword) {
		return passwordEncoder.matches(requestPassword, authDbPassword);
	}
}