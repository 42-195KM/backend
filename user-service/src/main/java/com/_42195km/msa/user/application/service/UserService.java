package com._42195km.msa.user.application.service;

import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;

public interface UserService {
	CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto);
}
