package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementKafkaConsumer {

    private final AlertContext<AchieveEventDto> context;
    private final AchievementStrategy achievementStrategy;

    @KafkaListener(topics = "achieve-achievement", groupId = "achieve-group")
    public void alertAchievement(Map<String, Object> eventMap) {
        context.sendMessage(eventMap, achievementStrategy, AchieveEventDto.class);
    }
}
