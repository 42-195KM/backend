package com._42195km.msa.auth.infrastructure.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 로그인 요청은 필터에서 건너뛰고, 컨트롤러에서 처리
		if (request.getRequestURI().equals("/api/v1/auth/login")) {
			log.info("로그인 요청 필터");
			filterChain.doFilter(request, response);
		} else {
			// 로그인 외의 요청은 필터를 통해 정상적으로 처리
			log.info("그 외의 요청 필터");
			filterChain.doFilter(request, response);
		}

	}

}
