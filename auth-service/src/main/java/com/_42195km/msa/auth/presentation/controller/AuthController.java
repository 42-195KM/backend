package com._42195km.msa.auth.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
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

	// TODO : logout , refresh 구현
	@PostMapping("/v1/auth/login")
	public ResponseEntity<ApiResponse<UserLogInResponseDto>> logIn(
		@RequestBody @Valid UserLogInRequestDto userLogInRequestDto,
		HttpServletResponse response
	) {

		UserLogInResponseDto userLogInResponseDto = authServiceimpl.logIn(userLogInRequestDto);

		response.setHeader("Authorization", userLogInResponseDto.getAccessToken());
		response.setHeader("Refresh-Token", userLogInResponseDto.getRefreshToken());
		response.setContentType("application/json");

		log.info("logIn response: {}", userLogInResponseDto.getAccessToken());

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
}
