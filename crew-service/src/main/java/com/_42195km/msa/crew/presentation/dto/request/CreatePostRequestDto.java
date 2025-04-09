package com._42195km.msa.crew.presentation.dto.request;

import com._42195km.msa.crew.application.dto.request.CreatePostCommandDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatePostRequestDto {
	@NotBlank
	@Schema(example = "2025년 4월 둘째주 정규런 공지")
	private String title;

	@NotBlank
	@Schema(example = "2025년 4월 둘째주 정규런은 여의도 공원에서 19시에 진행합니다.")
	private String content;

	@Schema(example = "정규런")
	private String hashtag;

	public CreatePostCommandDto toCommandDto() {
		return new CreatePostCommandDto(title, content, hashtag);
	}

}
