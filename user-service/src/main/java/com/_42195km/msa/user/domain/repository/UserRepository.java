package com._42195km.msa.user.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.user.domain.model.User;

import jakarta.validation.constraints.NotBlank;

public interface UserRepository {

	User save(User user);

	Page<User> findAllByIsDeletedIsFalse(Pageable pageable);

	Optional<User> findByIdAndIsDeletedIsFalse(UUID userId);

	Page<User> searchUser(String keyword, Pageable pageable);

	boolean findByUserName(@NotBlank(message = "유저 이름은 공백일 수 없습니다.") String username);
}
