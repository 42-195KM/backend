package com._42195km.msa.user.application.dto.request;

import java.sql.Date;

import com._42195km.msa.user.domain.model.Gender;
import com._42195km.msa.user.domain.model.User;
import com._42195km.msa.user.domain.model.UserRole;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CreateUserRequestDto {

	@NotBlank(message = "유저 이름은 공백일 수 없습니다.")
	private String username;

	@NotBlank(message = "비밀번호는 공백일 수 없습니다.")
	private String password;

	@NotBlank(message = "이메일은 공백일 수 없습니다.")
	@Pattern(
		regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
		message = "올바른 이메일 형식이 아닙니다."
	)
	private String email;

	@Pattern(
		regexp = "^\\d{4}-\\d{2}-\\d{2}$",
		message = "생년월일은 yyyy-MM-dd 형식으로 입력해주세요."
	)
	private String birth;

	@NotNull(message = "성별은 NULL일 수 없습니다.")
	private Gender gender;

	@NotNull(message = "유저 역할은 NULL일 수 없습니다.")
	private UserRole role;

	@NotBlank(message = "슬랙아이디는 공백일 수 없습니다.")
	private String mediaId;

	@Pattern(
		regexp = "^010-\\d{4}-\\d{4}$",
		message = "전화번호 형식은 010-1234-5678 이어야 합니다."
	)
	private String phone;

	public static User toUser(CreateUserRequestDto createUserRequestDto, String encodedPassword) {

		return User.builder()
			.username(createUserRequestDto.getUsername())
			.password(encodedPassword)
			.email(createUserRequestDto.getEmail())
			.birth(Date.valueOf(createUserRequestDto.getBirth()))
			.gender(createUserRequestDto.getGender())
			.role(createUserRequestDto.getRole())
			.mediaId(createUserRequestDto.getMediaId())
			.phone(createUserRequestDto.getPhone())
			.build();
	}
}
