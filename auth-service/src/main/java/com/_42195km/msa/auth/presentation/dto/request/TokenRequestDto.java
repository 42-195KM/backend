package com._42195km.msa.auth.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class TokenRequestDto {

	@NotBlank(message = "검증 토큰은 필수입니다.")
	private String token;
}
