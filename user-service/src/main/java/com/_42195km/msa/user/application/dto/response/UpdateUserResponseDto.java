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
@AllArgsConstructor
@Getter
public class UpdateUserResponseDto {

	private UUID id;

	private String username;

	// private String password;

	private String email;

	private Date birth;

	private Gender gender;

	private UserRole role;

	private String mediaId;

	private String phone;

	public static UpdateUserResponseDto fromUser(User targetUser) {

		return UpdateUserResponseDto.builder()
			.id(targetUser.getId())
			.username(targetUser.getUsername())
			// .password(targetUser.getPassword())
			.email(targetUser.getEmail())
			.birth(targetUser.getBirth())
			.gender(targetUser.getGender())
			.role(targetUser.getRole())
			.mediaId(targetUser.getMediaId())
			.phone(targetUser.getPhone())
			.build();
	}
}
