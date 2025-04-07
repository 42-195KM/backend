package com._42195km.msa.crew.application.service;

import org.springframework.stereotype.Service;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.common.exception.code.CommonErrorCode;
import com._42195km.msa.crew.application.dto.request.CreatePostCommandDto;
import com._42195km.msa.crew.domain.model.Post;
import com._42195km.msa.crew.domain.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final PostRepository postRepository;

	public void createPost(CreatePostCommandDto commandDto) {
		try {
			Post post = new Post(commandDto.getTitle(), commandDto.getContent(), commandDto.getHashtag());
			postRepository.save(post);
		} catch (Exception e) {
			throw CustomBusinessException.from(CommonErrorCode.CREW_BOARD_CREATE_POST_FAILED);
		}
	}
}
