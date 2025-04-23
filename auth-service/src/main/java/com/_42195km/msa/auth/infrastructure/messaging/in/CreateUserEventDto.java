package com._42195km.msa.auth.infrastructure.messaging.in;

import java.util.UUID;

import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreateUserEventDto {

	private UUID userId;
	private String username;
	private String password;
	private UserRole role;

	public static Auth toAuth(CreateUserEventDto createUserEventDto) {

		return Auth.builder()
			.userUuid(createUserEventDto.getUserId())
			.username(createUserEventDto.getUsername())
			.password(createUserEventDto.getPassword())
			.role(createUserEventDto.getRole())
			.build();
	}
}
