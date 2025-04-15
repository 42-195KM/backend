package com._42195km.msa.gateway.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TokenRequestDto {
	
	private String token;
}
