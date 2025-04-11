package com._42195km.msa.user.application.dto.response;

import java.util.Date;
import java.util.UUID;

import com._42195km.msa.user.domain.model.Gender;
import com._42195km.msa.user.domain.model.User;
import com._42195km.msa.user.domain.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetAllUserResponseDto {

	private UUID id;

	private String username;

	private String password;

	private String email;

	private Date birth;

	private Gender gender;

	private UserRole role;

	private String mediaId;

	private String phone;

	public static GetAllUserResponseDto fromUser(User user) {

		return GetAllUserResponseDto.builder()
			.id(user.getId())
			.username(user.getUsername())
			.password(user.getPassword())
			.email(user.getEmail())
			.birth(user.getBirth())
			.gender(user.getGender())
			.role(user.getRole())
			.mediaId(user.getMediaId())
			.phone(user.getPhone())
			.build();
	}
}
