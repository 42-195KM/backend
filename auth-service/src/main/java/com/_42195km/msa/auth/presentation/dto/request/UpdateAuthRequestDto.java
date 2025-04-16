package com._42195km.msa.auth.presentation.dto.request;

import java.util.UUID;

import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateAuthRequestDto {

	@NotNull
	private UUID userId;

	@NotBlank
	private String username;

	@NotBlank
	private String password;

	@NotNull
	private UserRole role;

	public static Auth toAuth(UpdateAuthRequestDto updateAuthRequestDto) {

		return Auth.builder()
			.userUuid(updateAuthRequestDto.getUserId())
			.username(updateAuthRequestDto.getUsername())
			.password(updateAuthRequestDto.getPassword())
			.role(updateAuthRequestDto.getRole())
			.build();
	}
}
