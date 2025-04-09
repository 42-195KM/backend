package com._42195km.msa.crew.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com._42195km.msa.crew.domain.model.Post;

public interface PostRepository {
	Optional<Post> findById(UUID postId);

	Page<Post> findAll(Pageable pageable);

	Page<Post> searchPosts(String keyword, Pageable pageable);

	Post save(Post post);

	void delete(Post post);
}
