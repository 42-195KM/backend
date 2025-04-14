package com._42195km.msa.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

	// WebFlux 기반 애플리케이션 -> SecurityWebFilterChain 사용
	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

		// 프론트가 붙거나 , CORS 에러 뜨면 CorsConfig 설정

		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.authorizeExchange(exchange -> exchange
				.pathMatchers("/api/v1/auth/login",
					"/api/v1/users",
					"/api/v1/auth/validate-token",
					"/swagger-ui/**",
					"/v3/api-docs/**",
					"/api/v1/auth/refresh").permitAll()
				.anyExchange().permitAll()
			)
			.build();
	}
}
