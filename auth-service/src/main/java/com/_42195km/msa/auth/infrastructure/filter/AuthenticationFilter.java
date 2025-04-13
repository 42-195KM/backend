package com._42195km.msa.auth.infrastructure.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
import com._42195km.msa.auth.application.dto.response.UserLogInResponseDto;
import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;
import com._42195km.msa.auth.infrastructure.filter.exception.FilterException;
import com._42195km.msa.auth.infrastructure.filter.success.FilterSuccess;
import com._42195km.msa.auth.infrastructure.jwt.JwtUtil;
import com._42195km.msa.auth.infrastructure.persistence.AuthRepositoryImpl;
import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.common.exception.CustomBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final AuthRepositoryImpl authRepositoryImpl;
	private final PasswordEncoder passwordEncoder;
	private final ObjectMapper objectMapper;
	// private final RedisService redisService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 로그인 요청시 필터 넘기기
		if (!request.getRequestURI().equals("/api/v1/auth/login")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 컨트롤러에서 로그인 엔드포인트가 없음 -> 직접 JSON BODY PARSING
		UserLogInRequestDto userLogInRequestDto = objectMapper.readValue(request.getInputStream(),
			UserLogInRequestDto.class);

		if (userLogInRequestDto == null) {
			throw new ServletException("유효하지 않은 요청입니다.");
		}

		// Auth-Service의 db 요청 (해당 유저가 있는지) -> User-Service와 동기화 중요
		Auth auth = authRepositoryImpl.findByUserName(userLogInRequestDto.getUsername())
			.orElseThrow(() -> CustomBusinessException.from(FilterException.NOT_FOUND_AUTH_USER));

		log.info("요청받은 비밀번호 ; {}", userLogInRequestDto.getPassword());
		log.info("db검증 비밀번호 : {}", auth.getPassword());

		// 비밀번호 검증
		if (!validatePassword(userLogInRequestDto.getPassword(), auth.getPassword())) {
			throw CustomBusinessException.from(FilterException.LOGIN_FAILED_WORNG_PASSWORD);
		}

		UUID userId = auth.getUserUuid();
		String userName = auth.getUsername();
		UserRole role = auth.getRole();

		String accessToken = jwtUtil.createAccessToken(userId, userName, role);
		String refreshToken = jwtUtil.createRefreshToken(userId);

		// TODO : 레디스에 저장
		//  redisTokenService.saveOrUpdateToken(userId, refreshToken);

		response.setHeader("Authorization", accessToken);
		response.setHeader("Refresh-Token", refreshToken);
		response.setContentType("application/json");

		UserLogInResponseDto userLogInResponseDto = UserLogInResponseDto.
			builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();

		objectMapper.writeValue(
			response.getOutputStream(),
			ResponseEntity.ok(
				ApiResponse
					.builder()
					.status(FilterSuccess.LOGIN_SUCCESS.getStatus())
					.code(FilterSuccess.LOGIN_SUCCESS.getCode())
					.message(FilterSuccess.LOGIN_SUCCESS.getMessage())
					.data(userLogInResponseDto)
					.build()
			)
		);
	}

	private boolean validatePassword(String requestPassword, String authDbPassword) {
		return passwordEncoder.matches(requestPassword, authDbPassword);
	}
}
