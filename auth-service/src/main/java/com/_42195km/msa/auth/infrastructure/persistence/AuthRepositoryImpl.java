package com._42195km.msa.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com._42195km.msa.auth.domain.model.Auth;
import com._42195km.msa.auth.domain.repository.AuthRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AuthRepositoryImpl implements AuthRepository {

	private final AuthJpaRepository authJpaRepository;

	@Override
	public Optional<Auth> findByUserName(String username) {
		return authJpaRepository.findByUsernameAndIsDeletedIsFalse(username);
	}

	@Override
	public Optional<Auth> findByUserUuid(UUID userUuId) {
		return authJpaRepository.findByUserUuidAndIsDeletedIsFalse(userUuId);
	}
}
