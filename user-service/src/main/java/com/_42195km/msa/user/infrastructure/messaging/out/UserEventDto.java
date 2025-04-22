package com._42195km.msa.user.infrastructure.messaging.out;

import java.util.UUID;

import com._42195km.msa.user.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class UserEventDto {

	private UUID userId;
	private String username;
	private String password;
	private UserRole role;
}
