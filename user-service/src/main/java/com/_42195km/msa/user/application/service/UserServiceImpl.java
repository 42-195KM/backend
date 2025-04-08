package com._42195km.msa.user.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
import com._42195km.msa.user.domain.exception.UserException;
import com._42195km.msa.user.domain.model.User;
import com._42195km.msa.user.infrastructure.persistence.UserRepositoryImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepositoryImpl userRepositoryImpl;

	@Override
	@Transactional
	public CreateUserResponseDto createUser(CreateUserRequestDto createUserRequestDto) {

		// TODO : 닉네임 중복 확인

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(createUserRequestDto.getPassword());

		User user = CreateUserRequestDto.toUser(createUserRequestDto, encodedPassword);

		User savedUser = userRepositoryImpl.save(user);

		return CreateUserResponseDto.fromUser(savedUser);
	}

	@Override
	public Page<GetAllUserResponseDto> getAllUsers(Pageable pageable) {

		Page<User> users = userRepositoryImpl.findAllByIsDeletedIsFalse(pageable);

		if (users.isEmpty()) {
			throw CustomBusinessException.from(UserException.NOT_FOUND_USER_LIST);
		}

		Page<GetAllUserResponseDto> getAllUserResponseDtos = users.map(
			GetAllUserResponseDto::fromUser
		);

		return getAllUserResponseDtos;
	}
}
