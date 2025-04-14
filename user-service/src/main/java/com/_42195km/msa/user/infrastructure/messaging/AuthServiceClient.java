package com._42195km.msa.user.infrastructure.messaging;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com._42195km.msa.user.presentation.dto.request.AuthUserCreateSyncRequestDto;
import com._42195km.msa.user.presentation.dto.request.AuthUserUpdateSyncRequestDto;

@FeignClient(name = "auth-service", path = "/api")
public interface AuthServiceClient {

	@PostMapping("/v1/auths")
	void syncCreateUser(@RequestBody AuthUserCreateSyncRequestDto authUserCreateSyncRequestDto);

	@PutMapping("/v1/app/auths")
	void syncUpdateUser(
		@RequestHeader("Authorization") String token,
		@RequestBody AuthUserUpdateSyncRequestDto authUserUpdateSyncRequestDto
	);

	@DeleteMapping("/v1/app/auths/{userId}")
	void syncDeleteUser(
		@RequestHeader("Authorization") String token,
		@PathVariable("userId") UUID userId
	);
}
