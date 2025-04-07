package com._42195km.msa.crew.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._42195km.msa.crew.domain.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
