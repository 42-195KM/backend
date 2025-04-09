package com._42195km.msa.user.application.dto.response;

import java.util.Date;
import java.util.UUID;

import com._42195km.msa.user.domain.model.Gender;
import com._42195km.msa.user.domain.model.User;
import com._42195km.msa.user.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CreateUserResponseDto {

	private UUID id;

	private String username;

	private String password;

	private String email;

	private Date birth;

	private Gender gender;

	private UserRole role;

	private String mediaId;

	private String phone;

	public static CreateUserResponseDto fromUser(User savedUser) {
		return CreateUserResponseDto.builder()
			.id(savedUser.getId())
			.username(savedUser.getUsername())
			.password(savedUser.getPassword())
			.email(savedUser.getEmail())
			.birth(savedUser.getBirth())
			.gender(savedUser.getGender())
			.role(savedUser.getRole())
			.mediaId(savedUser.getMediaId())
			.phone(savedUser.getPhone())
			.build();
	}
}
