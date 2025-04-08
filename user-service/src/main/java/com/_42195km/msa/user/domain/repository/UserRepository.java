package com._42195km.msa.user.domain.repository;

import com._42195km.msa.user.domain.model.User;

public interface UserRepository {
	User save(User user);
}
