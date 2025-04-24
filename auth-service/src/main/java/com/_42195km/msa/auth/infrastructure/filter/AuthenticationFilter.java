package com._42195km.msa.auth.infrastructure.filter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com._42195km.msa.auth.infrastructure.jwt.JwtUtil;

import io.jsonwebtoken.Claims;
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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 로그인 요청은 필터에서 건너뛰고, 컨트롤러에서 처리
		if (request.getRequestURI().equals("/api/v1/auth/login") ||
			request.getRequestURI().equals("/api/v1/auth/refresh") ||
			request.getRequestURI().equals("/api/v1/auths")
		) {
			log.info("요청 제외 필터");
			filterChain.doFilter(request, response);
			return;
		}

		// Authorization 헤더에서 토큰 추출
		String authHeader = request.getHeader("Authorization");

		log.info("Authorization header: {}", authHeader);

		if (authHeader != null) {
			jwtUtil.validateToken(authHeader);
			Claims claims = jwtUtil.parseClaims(authHeader);
			UUID userId = UUID.fromString(claims.getSubject());
			String role = claims.get("role", String.class);

			// SecurityContext에 인증 객체 설정
			UsernamePasswordAuthenticationToken authentication =
				new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority(role)));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("인증 성공: userId={}, role={}", userId, role);
		} else {
			log.warn("유효하지 않은 토큰 또는 토큰 없음");
		}

		filterChain.doFilter(request, response);

	}

}
