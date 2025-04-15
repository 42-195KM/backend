package com._42195km.msa.auth.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.auth.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ValidateTokenResponse {

	private UUID userId;
	private String userName;
	private UserRole role;

}
