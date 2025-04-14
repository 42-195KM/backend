package com._42195km.msa.auth.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateAuthResponseDto {

	private UUID id;
	private UUID userUuid;
	private String username;
	// private String password;
	private UserRole role;

	public static UpdateAuthResponseDto from(Auth auth) {

		return UpdateAuthResponseDto.builder()
			.id(auth.getId())
			.userUuid(auth.getUserUuid())
			.username(auth.getUsername())
			// .password(auth.getPassword())
			.role(auth.getRole())
			.build();
	}
}
