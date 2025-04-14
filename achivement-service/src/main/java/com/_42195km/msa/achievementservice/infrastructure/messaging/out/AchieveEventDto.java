package com._42195km.msa.achievementservice.infrastructure.messaging.out;

import java.util.UUID;

import com._42195km.msa.achievementservice.domain.model.AchievementUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AchieveEventDto {
	private UUID userId;
	private String userMediaId;
	private UUID achievementId;
	private String achievementTitle;
	private String achievementDescription;

	public static AchieveEventDto from(AchievementUser achievementUser) {
		return AchieveEventDto.builder()
			.userId(achievementUser.getUserId())
			.achievementId(achievementUser.getAchievement().getId())
			.achievementTitle(achievementUser.getAchievement().getTitle())
			.achievementDescription(achievementUser.getAchievement().getDescription())
			.build();
	}
}
