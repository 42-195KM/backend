package com._42195km.alertservice;

import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class KafkaConsumerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper ;
    private final LinkedBlockingQueue<Map<String,Object>> messageQueue = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "achieve-achievement-test", groupId = "achieve-group-test")
    void achievementConsumer(Map<String, Object> eventMap) {
        messageQueue.offer(eventMap);
    }

    @Test
    void AchievementProducerTest() {
        AchieveEventDto achieveEventDto = AchieveEventDto.builder()
                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .userMediaId("U08MRJYTBL1")
                .achievementId(UUID.fromString("aaaaaaaa-bbbb-cccc-b183-cad3a17bbf53"))
                .achievementTitle("test Title")
                .achievementDescription("test description")
                .build();
        kafkaTemplate.send("achieve-achievement-test", achieveEventDto);

        try {
            Map<String, Object> poll = messageQueue.poll(5, TimeUnit.SECONDS);
            System.out.println("컨슈머 소비 : " + poll);
            AchieveEventDto consumeDto = objectMapper.convertValue(poll, AchieveEventDto.class);
            Assertions.assertEquals(achieveEventDto.getUserMediaId(), consumeDto.getUserMediaId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "competition_notification-test", groupId = "competition-group-test")
    void competitionConsumer(Map<String, Object> eventMap) {
        messageQueue.offer(eventMap);
    }

    @Test
    void CompetitionProducerTest() {
        CompetitionEventDto competitionEventDto = CompetitionEventDto.builder()
                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .mediaId("U08MRJYTBL1")
                .title("test Title 대회")
                .build();
        kafkaTemplate.send("competition_notification-test", competitionEventDto);

        try {
            Map<String, Object> poll = messageQueue.poll(5, TimeUnit.SECONDS);
            System.out.println("컨슈머 소비 : " + poll);
            CompetitionEventDto consumeDto = objectMapper.convertValue(poll, CompetitionEventDto.class);
            Assertions.assertEquals(competitionEventDto.getMediaId(), consumeDto.getMediaId());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
