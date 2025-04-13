package com._42195km.msa.auth.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.auth.application.dto.request.BlackListRequestDto;
import com._42195km.msa.auth.application.dto.request.RefreshTokenRequestDto;
import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
import com._42195km.msa.auth.application.dto.response.AccessTokenReissueResponseDto;
import com._42195km.msa.auth.application.dto.response.UserLogInResponseDto;
import com._42195km.msa.auth.application.service.AuthServiceImpl;
import com._42195km.msa.auth.application.success.AuthSuccessCode;
import com._42195km.msa.common.api.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

	private final AuthServiceImpl authServiceimpl;

	@PostMapping("/v1/auth/login")
	public ResponseEntity<ApiResponse<UserLogInResponseDto>> logIn(
		@RequestBody @Valid UserLogInRequestDto userLogInRequestDto,
		HttpServletResponse response
	) {

		UserLogInResponseDto userLogInResponseDto = authServiceimpl.logIn(userLogInRequestDto);

		response.setHeader("Authorization", userLogInResponseDto.getAccessToken());
		response.setHeader("Refresh-Token", userLogInResponseDto.getRefreshToken());
		response.setContentType("application/json");

		return ResponseEntity.ok(
			ApiResponse
				.<UserLogInResponseDto>builder()
				.status(AuthSuccessCode.LOGIN_SUCCESS.getStatus())
				.code(AuthSuccessCode.LOGIN_SUCCESS.getCode())
				.message(AuthSuccessCode.LOGIN_SUCCESS.getMessage())
				.data(userLogInResponseDto)
				.build()
		);
	}

	@PostMapping("/v1/auth/refresh")
	public ResponseEntity<ApiResponse<AccessTokenReissueResponseDto>> refreshAccessToken(
		@RequestBody @Valid RefreshTokenRequestDto refreshTokenRequestDto
	) {
		AccessTokenReissueResponseDto accessTokenReissueResponseDto
			= authServiceimpl.refresh(refreshTokenRequestDto);

		return ResponseEntity
			.ok(
				ApiResponse
					.<AccessTokenReissueResponseDto>builder()
					.status(AuthSuccessCode.REFRESH_SUCCESS.getStatus())
					.code(AuthSuccessCode.REFRESH_SUCCESS.getCode())
					.message(AuthSuccessCode.REFRESH_SUCCESS.getMessage())
					.data(accessTokenReissueResponseDto)
					.build()
			);
	}

	@PostMapping("/v1/auth/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@RequestParam UUID userId) {

		authServiceimpl.logOut(userId);

		return ResponseEntity
			.ok(
				ApiResponse
					.<Void>builder()
					.status(AuthSuccessCode.LOGOUT_SUCCESS.getStatus())
					.code(AuthSuccessCode.LOGOUT_SUCCESS.getCode())
					.message(AuthSuccessCode.LOGOUT_SUCCESS.getMessage())
					.data(null)
					.build()
			);
	}

	@PostMapping("/v1/auth/blacklist")
	public ResponseEntity<ApiResponse<Void>> blackList(
		@RequestBody @Valid BlackListRequestDto blackListRequestDto
	) {

		authServiceimpl.blackList(blackListRequestDto);

		return ResponseEntity
			.ok(
				ApiResponse
					.<Void>builder()
					.status(AuthSuccessCode.BLACK_LIST_SUCCESS.getStatus())
					.code(AuthSuccessCode.BLACK_LIST_SUCCESS.getCode())
					.message(AuthSuccessCode.BLACK_LIST_SUCCESS.getMessage())
					.data(null)
					.build()
			);
	}
}
