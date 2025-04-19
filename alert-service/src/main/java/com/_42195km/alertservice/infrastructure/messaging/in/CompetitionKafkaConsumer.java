package com._42195km.alertservice.infrastructure.messaging.in;


import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompetitionKafkaConsumer {

    private final AlertContext context;
    private final CompetitionStrategy competitionStrategy;

    @KafkaListener(topics = "competition_notification", groupId = "competition-group")
    public void alertCompetition(Map<String, Object> eventMap) {
        context.sendMessage(eventMap, competitionStrategy, CompetitionEventDto.class);
    }

}
