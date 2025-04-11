package com._42195km.msa.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.auth.domain.model.Auth;

public interface AuthJpaRepository extends JpaRepository<Auth, UUID> {

	Optional<Auth> findByUsername(String username);
}
