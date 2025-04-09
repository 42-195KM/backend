package com._42195km.msa.user.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.request.UpdateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetUserResponseDto;
import com._42195km.msa.user.application.dto.response.SearchUserResponseDto;
import com._42195km.msa.user.application.dto.response.UpdateUserResponseDto;

import jakarta.validation.Valid;

public interface UserService {
	CreateUserResponseDto createUser(@Valid CreateUserRequestDto createUserRequestDto);

	Page<GetAllUserResponseDto> getAllUsers(Pageable pageable);

	GetUserResponseDto getUser(UUID userId);

	Page<SearchUserResponseDto> searchUserList(String keyword, Pageable pageable);

	UpdateUserResponseDto updateUser(UUID userId, @Valid UpdateUserRequestDto updateUserRequestDto);
}
