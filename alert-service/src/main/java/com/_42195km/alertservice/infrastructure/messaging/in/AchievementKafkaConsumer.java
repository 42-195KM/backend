package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.MessageService;
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
    @KafkaListener(topics = "achieve-achievement", groupId = "alert-group")
    public void AlertAchievement(Map<String, Object> eventMap) {


        AchieveEventDto achieveEventDto = objectMapper.convertValue(eventMap, AchieveEventDto.class);

        log.info("AchieveEventDto: {}", achieveEventDto);

    }




}
