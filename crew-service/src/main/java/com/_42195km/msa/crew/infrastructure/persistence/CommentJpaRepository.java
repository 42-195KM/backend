package com._42195km.msa.crew.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.crew.domain.model.Comment;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPostIdAndIsDeletedFalse(UUID postId);
	Optional<Comment> findByIdAndIsDeletedFalse(UUID commentId);

}
