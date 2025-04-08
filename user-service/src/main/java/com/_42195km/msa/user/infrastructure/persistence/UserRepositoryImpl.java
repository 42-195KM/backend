package com._42195km.msa.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.user.domain.model.User;
import com._42195km.msa.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	@Transactional
	public User save(User user) {

		return userJpaRepository.save(user);
	}

	@Override
	public Page<User> findAllByIsDeletedIsFalse(Pageable pageable) {

		return userJpaRepository.findAllByIsDeletedIsFalse(pageable);
	}

	@Override
	public Optional<User> findByIdAndIsDeletedIsFalse(UUID userId) {
		return userJpaRepository.findByIdAndIsDeletedIsFalse(userId);
	}

	@Override
	public Page<User> searchUser(String keyword, Pageable pageable) {
		return userJpaRepository.searchByKeyword(keyword, pageable);
	}
}
