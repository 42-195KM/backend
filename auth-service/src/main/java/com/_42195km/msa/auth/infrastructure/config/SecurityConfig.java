package com._42195km.msa.auth.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com._42195km.msa.auth.infrastructure.filter.AuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationFilter authenticationFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // CSRF 비활성화 (JWT 사용 시 필수)
			.formLogin(form -> form.disable())    // 기본 로그인 폼 비활성화
			.httpBasic(httpBasic -> httpBasic.disable())
			.sessionManagement(
				session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 STATELESS 모드로 설정
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/auth/login", "/api/v1/auth/refresh", "/api/v1/auth/validate-token",
					"/api/v1/auths",
					"/swagger-ui/**", "/v3/api-docs/**")
				.permitAll() // 로그인 및 토큰 갱신 API는 인증 없이 허용
				.anyRequest()
				.authenticated() // 나머지 모든 요청은 인증 필요
			)
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}