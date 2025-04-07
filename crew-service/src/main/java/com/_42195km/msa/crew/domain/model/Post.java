package com._42195km.msa.crew.domain.model;

import java.util.UUID;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.crew.application.dto.request.UpdatePostCommandDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_crew_post")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private String title;
	private String content;
	private String hashtag;

	public Post(String title, String content, String hashtag) {
		this.title = title;
		this.content = content;
		this.hashtag = hashtag;
	}

	public void update(UpdatePostCommandDto commandDto) {
		this.title = commandDto.getTitle();
		this.content = commandDto.getContent();
		this.hashtag = commandDto.getHashtag();
	}


}
