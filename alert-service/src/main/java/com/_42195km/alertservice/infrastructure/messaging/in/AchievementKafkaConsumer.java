package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementKafkaConsumer {

    private final AlertContext context;
    private final AchievementStrategyImpl achievementStrategy;

    @KafkaListener(topics = "achieve-achievement", groupId = "achieve-group")
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 1000, multiplier = 2.0), // 초기 간격은 1초로 재시도 간격이 2배씩 증가
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dead-t" // 기존 토픽 이름에서 -dead-t 접미사가 추가된 이름으로 Dead Letter Topic 생성
    )
    public void alertAchievement(Map<String, Object> eventMap) {
        context.sendMessage(eventMap, achievementStrategy, AchieveEventDto.class);
    }
}
