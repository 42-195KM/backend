package com._42195km.msa.crew.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.crew.domain.model.Post;
import com._42195km.msa.crew.domain.repository.PostRepository;

@Repository
public class PostRepositoryImpl implements PostRepository {

	private final PostJpaRepository postJpaRepository;

	public PostRepositoryImpl(PostJpaRepository postJpaRepository) {
		this.postJpaRepository = postJpaRepository;
	}

	@Override
	public Optional<Post> findById(UUID postId) {
		return postJpaRepository.findByIdAndIsDeletedFalse(postId);
	}

	@Override
	public Page<Post> findAll(Pageable pageable) {
		return postJpaRepository.findAll(pageable);
	}

	@Override
	public Page<Post> searchPosts(String keyword, Pageable pageable) {
		return postJpaRepository.searchPosts(keyword, pageable);
	}

	@Override
	public Post save(Post post) {
		return postJpaRepository.save(post);
	}

	/**
	 * TODO : 인증/인가 구현 후 수정
	 * @param post
	 */
	@Override
	@Transactional
	public void delete(Post post) {
		postJpaRepository.save(post);
	}
}
