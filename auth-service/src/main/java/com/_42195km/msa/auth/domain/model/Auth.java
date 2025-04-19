package com._42195km.msa.auth.domain.model;

import java.util.UUID;

import com._42195km.msa.auth.presentation.dto.request.UpdateAuthRequestDto;
import com._42195km.msa.common.BaseEntity;

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
@Table(name = "p_auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Auth extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID userUuid;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Builder
	public Auth(
		UUID userUuid,
		String username,
		String password,
		UserRole role
	) {
		this.userUuid = userUuid;
		this.username = username;
		this.password = password;
		this.role = role;
	}

	public void update(UpdateAuthRequestDto updateAuthRequestDto) {

		this.userUuid = updateAuthRequestDto.getUserId();
		this.username = updateAuthRequestDto.getUsername();
		this.password = updateAuthRequestDto.getPassword();
		this.role = updateAuthRequestDto.getRole();
	}
}
