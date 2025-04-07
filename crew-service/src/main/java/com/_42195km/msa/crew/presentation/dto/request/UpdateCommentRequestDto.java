package com._42195km.msa.crew.presentation.dto.request;

import com._42195km.msa.crew.application.dto.request.UpdateCommentCommandDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCommentRequestDto {
	@NotBlank
	@Schema(example = "저도 함께 하고 싶습니다! [수정]")
	private String comment;

	public UpdateCommentCommandDto toCommandDto() {
		return new UpdateCommentCommandDto(comment);
	}
}
