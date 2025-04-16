package com._42195km.msa.competitionservice.infrastructure.messaging;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompetitionApplyNotificationDto {
	private UUID userId;
	private String mediaId;
	private String title;
}
