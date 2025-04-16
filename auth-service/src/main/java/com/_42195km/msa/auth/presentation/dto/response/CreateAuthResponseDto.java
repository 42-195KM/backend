package com._42195km.msa.auth.presentation.dto.response;

import java.util.UUID;

import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CreateAuthResponseDto {

	private UUID id;
	private UUID userUuid;
	private String username;
	// private String password;
	private UserRole role;

	public static CreateAuthResponseDto from(Auth savedAuth) {

		return CreateAuthResponseDto.builder()
			.id(savedAuth.getId())
			.userUuid(savedAuth.getUserUuid())
			.username(savedAuth.getUsername())
			// .password(savedAuth.getPassword())
			.role(savedAuth.getRole())
			.build();
	}
}
