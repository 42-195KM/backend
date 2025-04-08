package com._42195km.msa.user.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.user.domain.model.User;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

	Page<User> findAllByIsDeletedIsFalse(Pageable pageable);

}
