package com._42195km.msa.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class AccessTokenReissueResponseDto {

	private String accessToken;
}
