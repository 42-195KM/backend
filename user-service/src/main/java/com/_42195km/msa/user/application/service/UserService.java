package com._42195km.msa.user.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;

public interface UserService {
	CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto);

	Page<GetAllUserResponseDto> getAllUsers(Pageable pageable);
}
