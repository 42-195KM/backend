package com._42195km.alertservice.infrastructure.messaging.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CompetitionEventDto {
    private UUID userId;
    private String mediaId;
    private String title;
}
