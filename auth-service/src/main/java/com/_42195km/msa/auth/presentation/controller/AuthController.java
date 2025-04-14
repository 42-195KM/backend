package com._42195km.msa.auth.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
import com._42195km.msa.auth.presentation.dto.request.CreateAuthRequestDto;
import com._42195km.msa.auth.presentation.dto.request.TokenRequestDto;
import com._42195km.msa.auth.presentation.dto.request.UpdateAuthRequestDto;
import com._42195km.msa.auth.presentation.dto.response.CreateAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.DeleteAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.UpdateAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.ValidateTokenResponse;
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

	@PostMapping("/v1/auth/validate-token")
	public ResponseEntity<ApiResponse<ValidateTokenResponse>> validateToken(
		@RequestBody @Valid TokenRequestDto tokenRequestDto
	) {

		ValidateTokenResponse validateTokenResponse = authServiceimpl.validateToken(tokenRequestDto);

		return ResponseEntity
			.ok(
				ApiResponse
					.<ValidateTokenResponse>builder()
					.status(AuthSuccessCode.VALIDATE_TOKEN_SUCCESS.getStatus())
					.code(AuthSuccessCode.VALIDATE_TOKEN_SUCCESS.getCode())
					.message(AuthSuccessCode.VALIDATE_TOKEN_SUCCESS.getMessage())
					.data(validateTokenResponse)
					.build()
			);
	}

	@PostMapping("/v1/auths")
	public ResponseEntity<ApiResponse<CreateAuthResponseDto>> createAuth(
		@RequestBody @Valid CreateAuthRequestDto createAuthRequestDto
	) {

		CreateAuthResponseDto createAuthResponseDto = authServiceimpl.createAuth(createAuthRequestDto);

		return ResponseEntity
			.ok(
				ApiResponse
					.<CreateAuthResponseDto>builder()
					.status(AuthSuccessCode.SYNC_SUCCESS.getStatus())
					.code(AuthSuccessCode.SYNC_SUCCESS.getCode())
					.message(AuthSuccessCode.SYNC_SUCCESS.getMessage())
					.data(createAuthResponseDto)
					.build()
			);
	}

	@PutMapping("/v1/app/auths")
	public ResponseEntity<ApiResponse<UpdateAuthResponseDto>> updateAuth(
		@RequestBody @Valid UpdateAuthRequestDto updateAuthRequestDto
	) {

		UpdateAuthResponseDto updateAuthResponseDto = authServiceimpl.updateAuth(updateAuthRequestDto);

		return ResponseEntity
			.ok(
				ApiResponse
					.<UpdateAuthResponseDto>builder()
					.status(AuthSuccessCode.SYNC_SUCCESS.getStatus())
					.code(AuthSuccessCode.SYNC_SUCCESS.getCode())
					.message(AuthSuccessCode.SYNC_SUCCESS.getMessage())
					.data(updateAuthResponseDto)
					.build()
			);
	}

	@DeleteMapping("/v1/app/auths/{userId}")
	public ResponseEntity<ApiResponse<DeleteAuthResponseDto>> deleteAuth(
		@PathVariable UUID userId
	) {

		authServiceimpl.deleteAuth(userId);

		return ResponseEntity
			.ok(
				ApiResponse
					.<DeleteAuthResponseDto>builder()
					.status(AuthSuccessCode.SYNC_SUCCESS.getStatus())
					.code(AuthSuccessCode.SYNC_SUCCESS.getCode())
					.message(AuthSuccessCode.SYNC_SUCCESS.getMessage())
					.data(null)
					.build()
			);
	}

}
