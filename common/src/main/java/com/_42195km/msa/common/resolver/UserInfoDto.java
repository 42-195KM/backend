package com._42195km.msa.common.resolver;

import java.util.UUID;

public record UserInfoDto(
	UUID userId,
	String role
) {
	public static UserInfoDto of(String userId, String role) {
		if (userId == null || role == null) {
			return empty();
		}
		return parseUserInfo(userId, role);
	}

	public static UserInfoDto empty() {
		return new UserInfoDto(null, null);
	}

	private static UserInfoDto parseUserInfo(String userId, String role) {
		try {
			return new UserInfoDto(UUID.fromString(userId), role);
		} catch (IllegalArgumentException e) {
			return empty();
		}
	}
}
