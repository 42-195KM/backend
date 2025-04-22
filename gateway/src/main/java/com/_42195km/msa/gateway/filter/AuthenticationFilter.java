package com._42195km.msa.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com._42195km.msa.gateway.dto.request.TokenRequestDto;
import com._42195km.msa.gateway.dto.response.ValidateTokenResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter implements GlobalFilter {

	// private final JwtUtil jwtUtil;
	private final WebClient.Builder webClientBuilder;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		// 1) 경로 및 메서드 확인
		String path = exchange.getRequest().getURI().getPath();
		String method = exchange.getRequest().getMethod().name();

		// 2) 인증 예외 경로 -> 필터 스킵
		if (isAllowedPath(path, method)) {
			return chain.filter(exchange);
		}

		String token = exchange.getRequest().getHeaders().getFirst("Authorization");

		// // 3) 헤더에서 JWT 추출
		// String token = jwtUtil.extractToken(exchange);
		//
		// // 4) JWT 검증 실패 시 401 반환
		// if (token == null || !jwtUtil.validateToken(token)) {
		// 	exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
		// 	return exchange.getResponse().setComplete();
		// }

		// Auth-Service로 책임 분리
		// 트-슈 : OpenFeing -> 순환참조문제 발생 / WebFlux기반  비동기식 구현
		return webClientBuilder.build()
			.post()
			.uri("lb://auth-service/api/v1/auth/validate-token")
			.header("Authorization", token)  // Authorization 헤더를 명시적으로 추가
			.bodyValue(TokenRequestDto.builder()
				.token(token)
				.build()
			)
			.retrieve()
			.bodyToMono(ValidateTokenResponseDto.class)
			.flatMap(response -> {
				log.info("검증 성공 - userId: {}, userName: {}, role: {}",
					response.getData().getUserId(),
					response.getData().getUserName(),
					response.getData().getRole()
				);

				ServerWebExchange mutatedExchange = exchange.mutate()
					.request(exchange.getRequest().mutate()
						.header("X-User-Id", response.getData().getUserId())
						.header("X-User-Name", response.getData().getUserName())
						.header("X-User-Role", response.getData().getRole())
						.build())
					.build();

				log.info("Mutated Exchange Headers: {}", mutatedExchange.getRequest().getHeaders());

				return chain.filter(mutatedExchange);
			})
			.onErrorResume(e -> {
				log.warn("토큰 검증 실패: {}", e.getMessage());
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			});
	}

	// 인증 없이 통과시킬 경로
	private boolean isAllowedPath(String path, String method) {
		return path.equals("/api/v1/auth/login") ||    // 로그인
			path.equals("/api/v1/auth/validate-token") || // 토큰검증
			path.equals("/api/v1/auth/refresh") || // 토큰 재발행
			path.equals("/api/v1/users") ||    // 회원가입
			path.equals("/api/v1/auths") ||
			path.startsWith("/swagger-ui") || // Swagger UI
			path.startsWith("/v3/api-docs"); // Swagger API Docs
	}

}
