package com._42195km.msa.user.application.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.user.application.dto.request.CreateUserRequestDto;
import com._42195km.msa.user.application.dto.request.UpdateUserRequestDto;
import com._42195km.msa.user.application.dto.response.CreateUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetAllUserResponseDto;
import com._42195km.msa.user.application.dto.response.GetUserResponseDto;
import com._42195km.msa.user.application.dto.response.SearchUserResponseDto;
import com._42195km.msa.user.application.dto.response.UpdateUserResponseDto;
import com._42195km.msa.user.application.exception.UserException;
import com._42195km.msa.user.domain.model.User;
import com._42195km.msa.user.infrastructure.messaging.AuthServiceClient;
import com._42195km.msa.user.infrastructure.persistence.UserRepositoryImpl;
import com._42195km.msa.user.presentation.dto.request.AuthUserCreateSyncRequestDto;
import com._42195km.msa.user.presentation.dto.request.AuthUserUpdateSyncRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepositoryImpl userRepositoryImpl;
	private final AuthServiceClient authServiceClient;
	private final HttpServletRequest request;

	@Override
	@Transactional
	public CreateUserResponseDto createUser(@Valid CreateUserRequestDto createUserRequestDto) {

		if (userRepositoryImpl.findByUserName(createUserRequestDto.getUsername())) {
			throw CustomBusinessException.from(UserException.DUPL_USER);
		}

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(createUserRequestDto.getPassword());

		User user = CreateUserRequestDto.toUser(createUserRequestDto, encodedPassword);

		User savedUser = userRepositoryImpl.save(user);

		// Auth-Service 동기화
		AuthUserCreateSyncRequestDto authUserCreateSyncRequestDto = AuthUserCreateSyncRequestDto.builder()
			.userId(savedUser.getId())
			.username(savedUser.getUsername())
			.password(encodedPassword)
			.role(savedUser.getRole())
			.build();

		authServiceClient.syncCreateUser(authUserCreateSyncRequestDto);

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

	@Override
	public GetUserResponseDto getUser(UUID userId) {

		User targetUser = findUserById(userId);

		return GetUserResponseDto.fromUser(targetUser);
	}

	@Override
	public Page<SearchUserResponseDto> searchUserList(String keyword, Pageable pageable) {

		Page<User> users = userRepositoryImpl.searchUser(keyword, pageable);

		if (users.isEmpty()) {
			throw CustomBusinessException.from(UserException.NOT_FOUND_USER_LIST);
		}

		Page<SearchUserResponseDto> searchUserResponseDtos = users.map(
			SearchUserResponseDto::fromUser
		);

		return searchUserResponseDtos;
	}

	@Transactional
	@Override
	public UpdateUserResponseDto updateUser(UUID userId, UpdateUserRequestDto updateUserRequestDto) {

		User targetUser = findUserById(userId);

		// 비밀번호가 들어온 경우에만 처리
		if (updateUserRequestDto.getPassword() != null && !updateUserRequestDto.getPassword().isBlank()) {
			boolean isSame = passwordEncoder.matches(updateUserRequestDto.getPassword(), targetUser.getPassword());

			if (!isSame) {
				String newEncodedPassword = passwordEncoder.encode(updateUserRequestDto.getPassword());
				targetUser.changePassword(newEncodedPassword);
			}
		}

		// 비밀번호 외의 필드 업데이트
		targetUser.update(updateUserRequestDto);

		AuthUserUpdateSyncRequestDto authUserUpdateSyncRequestDto = AuthUserUpdateSyncRequestDto.builder()
			.userId(targetUser.getId())
			.password(targetUser.getPassword())
			.username(targetUser.getUsername())
			.role(targetUser.getRole())
			.build();

		String header = request.getHeader("Authorization");
		authServiceClient.syncUpdateUser(header, authUserUpdateSyncRequestDto);

		return UpdateUserResponseDto.fromUser(targetUser);
	}

	@Transactional
	@Override
	public void deleteUser(UUID userId) {

		User targetUser = findUserById(userId);

		targetUser.setDeleted();

		String header = request.getHeader("Authorization");
		authServiceClient.syncDeleteUser(header, userId);
	}

	@Transactional
	@Override
	public void banUser(UUID userId) {

		User targetUser = findUserById(userId);

		targetUser.setDeleted();

		String header = request.getHeader("Authorization");
		authServiceClient.syncDeleteUser(header, userId);
	}

	private User findUserById(UUID userId) {

		User targetUser = userRepositoryImpl.findByIdAndIsDeletedIsFalse(userId)
			.orElseThrow(() -> CustomBusinessException.from(UserException.NOT_FOUND_USER));

		return targetUser;
	}
}
