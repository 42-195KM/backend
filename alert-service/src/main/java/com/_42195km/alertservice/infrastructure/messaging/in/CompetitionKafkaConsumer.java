package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionKafkaConsumer {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "competition_notification", groupId = "competition-group")
    public void AlertCompetition(Map<String, Object> eventMap) {
        CompetitionEventDto competitionEventDto = objectMapper.convertValue(eventMap, CompetitionEventDto.class);

        log.info("AchieveEventDto: {}", competitionEventDto);
        String message = competitionEventDto.getTitle() +" 신청이 완료되었습니다.";

        messageService.sendMessage(message,competitionEventDto.getMediaId());

    }



}
