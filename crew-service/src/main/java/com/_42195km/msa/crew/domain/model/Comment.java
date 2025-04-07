package com._42195km.msa.crew.domain.model;

import java.util.UUID;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.crew.application.dto.request.CreateCommentCommandDto;

import jakarta.persistence.Column;
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
@Table(name = "p_crew_comment")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(name = "post_id", columnDefinition = "uuid")
	private UUID postId ;
	private String comment;

	public static Comment create(UUID postId, CreateCommentCommandDto commandDto) {
		return Comment.builder()
			.postId(postId)
			.comment(commandDto.getComment()).build();
	}
}
