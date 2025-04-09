package com._42195km.msa.crew.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._42195km.msa.crew.domain.model.Post;

public interface PostJpaRepository extends JpaRepository<Post, Long> {

	Optional<Post> findByIdAndIsDeletedFalse(UUID userId);

	Page<Post> findAllByIsDeletedFalse(Pageable pageable);

	@Query("SELECT p FROM Post p " +
		"WHERE p.isDeleted = false AND " +
		"(p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR p.hashtag LIKE %:keyword%)")
	Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);
}
