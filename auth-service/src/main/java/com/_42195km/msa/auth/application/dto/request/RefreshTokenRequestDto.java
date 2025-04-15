package com._42195km.msa.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class RefreshTokenRequestDto {

	@NotBlank(message = "Refresh Token 이 필요합니다.")
	private String refreshToken;

}
