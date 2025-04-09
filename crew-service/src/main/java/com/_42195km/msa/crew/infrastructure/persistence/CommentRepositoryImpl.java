package com._42195km.msa.crew.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.crew.domain.model.Comment;
import com._42195km.msa.crew.domain.repository.CommentRepository;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
	private final CommentJpaRepository commentJpaRepository;

	public CommentRepositoryImpl(CommentJpaRepository commentJpaRepository) {
		this.commentJpaRepository = commentJpaRepository;
	}

	@Override
	public List<Comment> findByPostId(UUID postId) {
		return commentJpaRepository.findByPostIdAndIsDeletedFalse(postId);
	}

	@Override
	public Optional<Comment> findById(UUID commentId) {
		return commentJpaRepository.findByIdAndIsDeletedFalse(commentId);
	}

	@Override
	public Comment save(Comment comment) {
		return commentJpaRepository.save(comment);
	}

	/**
	 * TODO : 인증/인가 구현 후 수정
	 * @param comment
	 */
	@Override
	@Transactional
	public void delete(Comment comment) {
		commentJpaRepository.save(comment);
	}
}
