package com._42195km.msa.crew.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.crew.domain.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	Optional<Post> findByIdAndIsDeletedFalse(UUID userId);
}
