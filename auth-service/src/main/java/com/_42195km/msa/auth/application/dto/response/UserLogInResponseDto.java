package com._42195km.msa.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class UserLogInResponseDto {

	private String accessToken;
	private String refreshToken;
	
}
