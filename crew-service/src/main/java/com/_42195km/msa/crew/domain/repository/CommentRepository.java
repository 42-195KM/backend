package com._42195km.msa.crew.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com._42195km.msa.crew.domain.model.Comment;

public interface CommentRepository {
	List<Comment> findByPostId(UUID postId);

	Optional<Comment> findById(UUID commentId);

	Comment save(Comment comment);

	void delete(Comment comment);
}
