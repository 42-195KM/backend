package com._42195km.msa.user.presentation.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com._42195km.msa.user.application.dto.response.BanUserResponseDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.DeleteUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetUserResponseDto;
import com._42195km.msa.user.application.dto.response.SearchUserResponseDto;
import com._42195km.msa.user.application.dto.response.UpdateUserResponseDto;
import com._42195km.msa.user.application.service.UserService;
import com._42195km.msa.user.application.success.UserSuccessCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "유저 서비스", description = "유저 컨트롤러")
public class UserController {

	private final UserService userService;

	@GetMapping("/v1/users/test")
	@Operation(summary = "테스트", description = "헤더에 제대로 값이 전달 되었는지 확인용")
	public ResponseEntity<Void> debugHeaders(HttpServletRequest request) {
		String userId = request.getHeader("X-User-Id");
		System.out.println(">>>> Controller에서 받은 X-User-Id: " + userId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/v1/users")
	@Operation(summary = "유저 회원가입", description = "유저 회원가입은 아무나 가능")
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
	@Operation(summary = "모든 유저 조회", description = "유저 조회는 'MASTER' 만 가능")
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

	@GetMapping("/v1/app/users/{userId}")
	@Operation(summary = "단건 유저 조회", description = "유저 조회는 'MASTER' 만 가능")
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

	@GetMapping("/v1/app/users/search")
	@Operation(summary = "유저 키워드 검색", description = "유저 조회는 'MASTER' 만 가능\n 유저 검색 키워드는 '이름, 번호, 메일, 매체ID'")
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

	@PatchMapping("/v1/app/users/{userId}")
	@Operation(summary = "유저 정보 수정", description = "유저 정보 수정은 아무나 가능")
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

	@DeleteMapping("/v1/app/users/{userId}")
	@Operation(summary = "유저 회원 탈퇴", description = "개인 회원 탈퇴는 해당 유저만 가능")
	public ResponseEntity<ApiResponse<DeleteUserResponseDto>> deleteUser(
		@PathVariable UUID userId
	) {
		userService.deleteUser(userId);

		return ResponseEntity
			.ok(
				ApiResponse
					.<DeleteUserResponseDto>builder()
					.status(UserSuccessCode.DELETE_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.DELETE_USER_SUCCESS.getCode())
					.message(UserSuccessCode.DELETE_USER_SUCCESS.getMessage())
					.data(null)
					.build()
			);
	}

	@DeleteMapping("/v1/app/users/ban/{userId}")
	@Operation(summary = "유저 밴", description = "유저 밴은 'MASTER' 만 가능")
	public ResponseEntity<ApiResponse<BanUserResponseDto>> banUser(
		@PathVariable UUID userId
	) {
		userService.banUser(userId);

		return ResponseEntity
			.ok(
				ApiResponse
					.<BanUserResponseDto>builder()
					.status(UserSuccessCode.BAN_USER_SUCCESS.getStatusCode().value())
					.code(UserSuccessCode.BAN_USER_SUCCESS.getCode())
					.message(UserSuccessCode.BAN_USER_SUCCESS.getMessage())
					.data(null)
					.build()
			);
	}

}
