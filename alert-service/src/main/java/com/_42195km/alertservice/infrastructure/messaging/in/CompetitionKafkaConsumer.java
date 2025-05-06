package com._42195km.alertservice.infrastructure.messaging.in;


import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.exception.AlertCode;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;

import com._42195km.msa.common.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionKafkaConsumer {

    private final AlertContext context;
    private final CompetitionStrategyImpl competitionStrategy;

    @KafkaListener(topics = "competition_notification", groupId = "competition-group")
    public void alertCompetition(List<Map<String, Object>> eventMaps) {

        List<CompletableFuture<Void>> futures = eventMaps.stream()
                .map(eventMap -> CompletableFuture.runAsync(() -> {
                    context.sendMessage(eventMap, competitionStrategy, CompetitionEventDto.class);
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }


    @KafkaListener(topics = "competition_notification-dead-t", groupId = "competition-group-dlt")
    void achievementConsumerDead(Map<String, Object> eventMap) {
        try {

            MDC.put("status", "DLT");
            log.error("[DLT] 대회 신청 메시지 수신 - 재처리 필요, payload: {}", eventMap);
        }finally {
            MDC.clear();
        }
    }

}
