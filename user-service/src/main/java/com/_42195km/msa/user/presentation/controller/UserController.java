package com._42195km.msa.user.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.request.UpdateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetUserResponseDto;
import com._42195km.msa.user.application.dto.response.SearchUserResponseDto;
import com._42195km.msa.user.application.dto.response.UpdateUserResponseDto;
import com._42195km.msa.user.application.service.UserService;
import com._42195km.msa.user.application.success.UserSuccessCode;

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
				ApiResponse
					.<Page<GetAllUserResponseDto>>builder()
					.status(UserSuccessCode.FIND_ALL_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.FIND_ALL_USER_SUCCESS.getCode())
					.message(UserSuccessCode.FIND_ALL_USER_SUCCESS.getMessage())
					.data(getUserResponseDto)
					.build()
			);
	}

	@GetMapping("/v1/users/{userId}")
	public ResponseEntity<ApiResponse<GetUserResponseDto>> getUser(
		@PathVariable UUID userId
	) {

		GetUserResponseDto getUserResponseDto = userService.getUser(userId);

		return ResponseEntity
			.ok(
				ApiResponse
					.<GetUserResponseDto>builder()
					.status(UserSuccessCode.FIND_ONE_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.FIND_ONE_USER_SUCCESS.getCode())
					.message(UserSuccessCode.FIND_ONE_USER_SUCCESS.getMessage())
					.data(getUserResponseDto)
					.build()
			);
	}

	@GetMapping("/v1/users/search")
	public ResponseEntity<ApiResponse<Page<SearchUserResponseDto>>> searchUser(
		@RequestParam String keyword,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable
	) {

		Page<SearchUserResponseDto> searchList = userService.searchUserList(keyword, pageable);

		return ResponseEntity
			.ok(
				ApiResponse
					.<Page<SearchUserResponseDto>>builder()
					.status(UserSuccessCode.FIND_KEYWORD_USER_LIST_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.FIND_KEYWORD_USER_LIST_SUCCESS.getCode())
					.message(UserSuccessCode.FIND_KEYWORD_USER_LIST_SUCCESS.getMessage())
					.data(searchList)
					.build()
			);
	}

	@PatchMapping("/v1/users/{userId}")
	public ResponseEntity<ApiResponse<UpdateUserResponseDto>> updateUser(
		@PathVariable UUID userId,
		@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto
	) {

		UpdateUserResponseDto updateUserResponseDto = userService.updateUser(userId, updateUserRequestDto);

		return ResponseEntity
			.ok(
				ApiResponse
					.<UpdateUserResponseDto>builder()
					.status(UserSuccessCode.UPDATE_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.UPDATE_USER_SUCCESS.getCode())
					.message(UserSuccessCode.UPDATE_USER_SUCCESS.getMessage())
					.data(updateUserResponseDto)
					.build()
			);
	}
	
	/*
		TODO : softDelete는 Auth 구현 후 AuditorAwareImpl이 잘 동작하면 생성
	 */

}
