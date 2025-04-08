package com._42195km.msa.user.presentation.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
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

	@GetMapping("/v1/app/users")
	public ResponseEntity<ApiResponse<Page<GetAllUserResponseDto>>> getAllUser(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {

		Page<GetAllUserResponseDto> getUserResponseDto = userService.getAllUsers(pageable);

		return ResponseEntity
			.ok(
				ApiResponse.<Page<GetAllUserResponseDto>>builder()
					.status(UserSuccessCode.FIND_ALL_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.FIND_ALL_USER_SUCCESS.getCode())
					.message(UserSuccessCode.FIND_ALL_USER_SUCCESS.getMessage())
					.data(getUserResponseDto)
					.build()
			);
	}
}
