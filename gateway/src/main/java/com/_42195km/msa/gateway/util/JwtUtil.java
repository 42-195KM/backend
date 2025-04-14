// package com._42195km.msa.gateway.util;
//
// import java.security.Key;
// import java.util.Base64;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
//
// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.JwtException;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.security.Keys;
// import jakarta.annotation.PostConstruct;
// import jakarta.ws.rs.core.HttpHeaders;
//
// @Component
// public class JwtUtil {
//
// 	@Value("${service.jwt.secret-key}")
// 	private String secretKey;
//
// 	private Key key;
//
// 	@PostConstruct
// 	public void init() {
// 		byte[] decoded = Base64.getDecoder().decode(secretKey);
// 		this.key = Keys.hmacShaKeyFor(decoded);
// 	}
//
// 	// "Bearer " 제거 + 토큰 추출
// 	public String extractToken(ServerWebExchange exchange) {
// 		String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
// 		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
// 			return null;
// 		}
// 		return authHeader.substring(7);
// 	}
//
// 	// 토큰 유효성 검사
// 	public boolean validateToken(String token) {
// 		try {
// 			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
// 			return true;
// 		} catch (JwtException e) {
// 			return false;
// 		}
// 	}
//
// 	// Claims(유저 정보) 파싱
// 	public Claims parseClaims(String token) {
// 		return Jwts.parserBuilder()
// 			.setSigningKey(key)
// 			.build()
// 			.parseClaimsJws(token)
// 			.getBody();
// 	}
// }
//
