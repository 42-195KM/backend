package com._42195km.msa.achievementservice.infrastructure.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com._42195km.msa.common.api.ApiResponse;

@FeignClient(name = "user-service", url = "http://localhost:19091/api/")
public interface UserClient {
	@GetMapping("/v1/app/users/{userId}")
	ResponseEntity<ApiResponse<UserMediaIdDto>> getUser(@PathVariable("userId") UUID userId);
}
