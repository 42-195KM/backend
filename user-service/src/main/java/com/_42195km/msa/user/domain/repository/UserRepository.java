package com._42195km.msa.user.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.user.domain.model.User;

public interface UserRepository {

	User save(User user);

	Page<User> findAllByIsDeletedIsFalse(Pageable pageable);
}
