package com._42195km.msa.auth.infrastructure.messaging.in;

import java.util.UUID;

import com._42195km.msa.auth.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class UpdateUserEventDto {

	private UUID userId;
	private String username;
	private String password;
	private UserRole role;
}
