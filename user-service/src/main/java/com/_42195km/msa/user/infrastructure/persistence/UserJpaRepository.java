package com._42195km.msa.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.user.domain.model.User;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

	Page<User> findAllByIsDeletedIsFalse(Pageable pageable);

	Optional<User> findByIdAndIsDeletedIsFalse(UUID userId);

	@Query("SELECT u FROM User u WHERE " +
		"LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		"LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		"u.phone LIKE CONCAT('%', :keyword, '%') OR " +
		"u.mediaId LIKE CONCAT('%', :keyword, '%')")
	Page<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	boolean existsByUsernameAndIsDeletedIsFalse(String username);
}
