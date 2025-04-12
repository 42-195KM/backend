package com._42195km.msa.auth.application.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
import com._42195km.msa.auth.application.dto.response.UserLogInResponseDto;
import com._42195km.msa.auth.application.exception.AuthException;
import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;
import com._42195km.msa.auth.infrastructure.jwt.JwtUtil;
import com._42195km.msa.auth.infrastructure.persistence.AuthRepositoryImpl;
import com._42195km.msa.common.exception.CustomBusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final JwtUtil jwtUtil;
	private final AuthRepositoryImpl authRepositoryImpl;
	private final PasswordEncoder passwordEncoder;

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

		UserLogInResponseDto userLogInResponseDto = UserLogInResponseDto.
			builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();

		log.info("access token: {}", userLogInResponseDto.getAccessToken());

		return userLogInResponseDto;
	}

	private boolean validatePassword(String requestPassword, String authDbPassword) {
		return passwordEncoder.matches(requestPassword, authDbPassword);
	}
}
