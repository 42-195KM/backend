package com._42195km.msa.auth.application.service;

import com._42195km.msa.auth.application.dto.request.RefreshTokenRequestDto;
import com._42195km.msa.auth.application.dto.request.UserLogInRequestDto;
import com._42195km.msa.auth.application.dto.response.AccessTokenReissueResponseDto;
import com._42195km.msa.auth.application.dto.response.UserLogInResponseDto;

import jakarta.validation.Valid;

public interface AuthService {

	UserLogInResponseDto logIn(@Valid UserLogInRequestDto userLogInRequestDto);

	AccessTokenReissueResponseDto refresh(@Valid RefreshTokenRequestDto refreshTokenRequestDto);
}
