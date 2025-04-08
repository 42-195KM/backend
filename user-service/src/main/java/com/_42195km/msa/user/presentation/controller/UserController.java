package com._42195km.msa.user.presentation.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.service.UserService;
import com._42195km.msa.user.domain.success.UserSuccessCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	@PostMapping("/v1/users")
	public ResponseEntity<ApiResponse<CreateUserResponseDto>> createUser(
		@RequestBody @Valid CreateUserRequestDto createUserRequestDto
	) {
		CreateUserResponseDto createUserResponseDto = userService.createUser(createUserRequestDto);

		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/api/v1/users")
			.build()
			.toUri();

		return ResponseEntity
			.created(location)
			.body(
				ApiResponse
					.<CreateUserResponseDto>builder()
					.status(UserSuccessCode.CREATE_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.CREATE_USER_SUCCESS.getCode())
					.message(UserSuccessCode.CREATE_USER_SUCCESS.getMessage())
					.data(createUserResponseDto)
					.build()
			);
	}
}
