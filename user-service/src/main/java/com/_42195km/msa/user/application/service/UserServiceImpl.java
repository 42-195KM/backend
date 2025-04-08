package com._42195km.msa.user.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
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
}
