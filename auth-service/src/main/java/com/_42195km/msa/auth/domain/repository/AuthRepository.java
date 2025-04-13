package com._42195km.msa.auth.domain.repository;

import java.util.Optional;

import com._42195km.msa.auth.domain.model.Auth;

public interface AuthRepository {
	Optional<Auth> findByUserName(String username);
}
