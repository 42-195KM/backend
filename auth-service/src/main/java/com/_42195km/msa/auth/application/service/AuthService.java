package com._42195km.msa.auth.application.service;

import java.util.UUID;

import com._42195km.msa.auth.application.dto.request.BlackListRequestDto;
import com._42195km.msa.auth.application.dto.request.RefreshTokenRequestDto;
import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
import com._42195km.msa.auth.application.dto.response.AccessTokenReissueResponseDto;
import com._42195km.msa.auth.application.dto.response.UserLogInResponseDto;
import com._42195km.msa.auth.presentation.dto.request.CreateAuthRequestDto;
import com._42195km.msa.auth.presentation.dto.request.TokenRequestDto;
import com._42195km.msa.auth.presentation.dto.request.UpdateAuthRequestDto;
import com._42195km.msa.auth.presentation.dto.response.CreateAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.UpdateAuthResponseDto;
import com._42195km.msa.auth.presentation.dto.response.ValidateTokenResponse;

import jakarta.validation.Valid;

public interface AuthService {

	UserLogInResponseDto logIn(@Valid UserLogInRequestDto userLogInRequestDto);

	AccessTokenReissueResponseDto refresh(@Valid RefreshTokenRequestDto refreshTokenRequestDto);

	void logOut(UUID userId);

	void blackList(@Valid BlackListRequestDto blackListRequestDto);

	ValidateTokenResponse validateToken(@Valid TokenRequestDto tokenRequestDto);

	CreateAuthResponseDto createAuth(@Valid CreateAuthRequestDto createAuthRequestDto);

	UpdateAuthResponseDto updateAuth(@Valid UpdateAuthRequestDto updateAuthRequestDto);

	void deleteAuth(UUID userId);
}
