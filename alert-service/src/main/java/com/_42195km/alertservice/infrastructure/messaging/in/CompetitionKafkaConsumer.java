package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.code.AlertCode;
import com._42195km.msa.common.exception.CustomBusinessException;
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
        try{
            CompetitionEventDto competitionEventDto = objectMapper.convertValue(eventMap, CompetitionEventDto.class);

            String message = competitionEventDto.getTitle() +" 신청이 완료되었습니다.";

            messageService.sendMessage(message,competitionEventDto.getMediaId());
        } catch (Exception e) {
            log.error("대회 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
            throw CustomBusinessException.from(AlertCode.COMPETITION_CONSUMER_ERROR);
        }

    }



}
