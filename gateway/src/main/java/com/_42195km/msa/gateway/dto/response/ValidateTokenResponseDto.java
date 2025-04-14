package com._42195km.msa.gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ValidateTokenResponseDto {

	private String code;
	private TokenData data;
	private String message;
	private int status;

	@Builder
	@Getter
	@AllArgsConstructor
	public static class TokenData {
		private String userId;
		private String userName;
		private String role;
	}

}
