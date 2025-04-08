package com._42195km.msa.user.application.dto.request;

import com._42195km.msa.user.domain.model.Gender;
import com._42195km.msa.user.domain.model.UserRole;
import com._42195km.msa.user.infrastructure.config.OptionalNotBlank;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UpdateUserRequestDto {

	@OptionalNotBlank(message = "username이 공백일 수 없습니다.")
	private String username;

	@OptionalNotBlank(message = "password가 공백일 수 없습니다.")
	private String password;

	@Pattern(
		regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
		message = "올바른 이메일 형식이 아닙니다."
	)
	private String email;

	@Pattern(
		regexp = "^\\d{4}-\\d{2}-\\d{2}$",
		message = "생년월일은 yyyy-MM-dd 형식으로 입력해주세요."
	)

	@OptionalNotBlank(message = "birth 공백일 수 없습니다.")
	private String birth;

	private Gender gender;

	private UserRole role;

	@OptionalNotBlank(message = "mediaId 공백일 수 없습니다.")
	private String mediaId;

	@Pattern(
		regexp = "^010-\\d{4}-\\d{4}$",
		message = "전화번호 형식은 010-xxxx-xxxx 이어야 합니다."
	)
	private String phone;
}
