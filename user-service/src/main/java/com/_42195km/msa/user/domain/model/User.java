package com._42195km.msa.user.domain.model;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import com._42195km.msa.common.BaseEntity;
import com._42195km.msa.user.application.dto.request.UpdateUserRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user", schema = "userschema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private Date birth;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	// 매체가 정해지면 추가
	@Column(nullable = false)
	private String mediaId;

	@Column(nullable = false)
	private String phone;

	@Builder
	public User(
		String username,
		String password,
		String email,
		Date birth,
		Gender gender,
		UserRole role,
		String mediaId,
		String phone
	) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.birth = birth;
		this.gender = gender;
		this.role = role;
		this.mediaId = mediaId;
		this.phone = phone;
	}

	public void update(UpdateUserRequestDto updateUserRequestDto) {

		Optional.ofNullable(updateUserRequestDto.getUsername()).ifPresent(value -> this.username = value);
		Optional.ofNullable(updateUserRequestDto.getPassword()).ifPresent(value -> this.password = value);
		Optional.ofNullable(updateUserRequestDto.getEmail()).ifPresent(value -> this.email = value);
		Optional.ofNullable(updateUserRequestDto.getGender()).ifPresent(value -> this.gender = value);
		Optional.ofNullable(updateUserRequestDto.getRole()).ifPresent(value -> this.role = value);
		Optional.ofNullable(updateUserRequestDto.getMediaId()).ifPresent(value -> this.mediaId = value);
		Optional.ofNullable(updateUserRequestDto.getPhone()).ifPresent(value -> this.phone = value);

	}
}
