package com._42195km.msa.user.presentation.dto.request;

import java.util.UUID;

import com._42195km.msa.user.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AuthUserUpdateSyncRequestDto {

	private UUID userId;
	private String username;
	private String password;
	private UserRole role;
	
}
