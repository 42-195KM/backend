package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementKafkaConsumer {

    @Qualifier("slackApiExecutor")
    private final ExecutorService executorService;
    private final AlertContext context;
    private final AchievementStrategyImpl achievementStrategy;

    @KafkaListener(topics = "achieve-achievement", groupId = "achieve-group")
    public void alertAchievement(List<Map<String, Object>> eventMaps) {
        List<CompletableFuture<Void>> futures = eventMaps.stream()
                .map(eventMap -> CompletableFuture.runAsync(() -> {
                    context.sendMessage(eventMap, achievementStrategy, AchieveEventDto.class);
                }, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    @KafkaListener(topics = "achieve-achievement-dead-t", groupId = "achieve-group-retry")
    void achievementConsumerDead(Map<String, Object> eventMap) {
        log.error("[DLT] 메시지 수신 - 재처리 필요, payload: {}", eventMap);
    }



}
