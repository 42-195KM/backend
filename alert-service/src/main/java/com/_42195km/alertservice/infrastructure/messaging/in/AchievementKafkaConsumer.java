package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.code.AlertCode;
import com._42195km.msa.common.exception.CustomBusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementKafkaConsumer {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "achieve-achievement", groupId = "achieve-group")
    public void alertAchievement(Map<String, Object> eventMap) {
        try {
            AchieveEventDto achieveEventDto = objectMapper.convertValue(eventMap, AchieveEventDto.class);

            String message = "축하합니다! " + achieveEventDto.getAchievementTitle() +" 업적을 달성하였습니다!!";

            messageService.sendMessage(message,achieveEventDto.getUserMediaId());
        } catch (Exception e) {
            log.error("업적 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
            throw CustomBusinessException.from(AlertCode.ACHIEVEMENT_CONSUMER_ERROR);
        }
    }
}
