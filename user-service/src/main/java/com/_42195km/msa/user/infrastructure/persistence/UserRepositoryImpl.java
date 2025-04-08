package com._42195km.msa.user.infrastructure.persistence;

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
}
