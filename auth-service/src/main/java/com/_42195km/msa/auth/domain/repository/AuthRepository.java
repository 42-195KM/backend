package com._42195km.msa.auth.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com._42195km.msa.auth.domain.model.Auth;

public interface AuthRepository {
	Optional<Auth> findByUserName(String username);

	Optional<Auth> findByUserUuid(UUID userUuId);
}
