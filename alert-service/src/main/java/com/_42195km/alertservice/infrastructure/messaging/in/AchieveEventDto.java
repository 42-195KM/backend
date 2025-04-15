package com._42195km.alertservice.infrastructure.messaging.in;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class AchieveEventDto {
    private UUID userId;
    private String userMediaId;
    private UUID achievementId;
    private String achievementTitle;
    private String achievementDescription;

}