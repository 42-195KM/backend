package com._42195km.alertservice;

import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;
import com._42195km.alertservice.infrastructure.messaging.in.AchievementStrategyImpl;
import com._42195km.alertservice.infrastructure.messaging.in.CompetitionStrategyImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Import(AchieveKafkaConsumerTest.TestConfig.class)
@DirtiesContext
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class AchieveKafkaConsumerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private AlertContext context;
    @Autowired
    private MessageService messageService;
    private final LinkedBlockingQueue<Map<String,Object>> messageQueue = new LinkedBlockingQueue<>();

    @BeforeEach
    void setUp() {
        messageQueue.clear();
    }

    @TestConfiguration
    static class TestConfig{
        @Bean
        AlertContext alertContext(ObjectMapper objectMapper, MessageService messageService) {
            return new AlertContext(objectMapper, messageService);
        }
        @Bean
        MessageService messageService() {
            return new MockMessageService();
        }
    }


    @KafkaListener(topics = "achieve-achievement-test", groupId = "achieve-group-test")
    void achievementConsumer(Map<String, Object> eventMap) {
        messageQueue.offer(eventMap);
    }
//    @Test
//    @DisplayName("업적 메시지 전송 테스트")
//    void AchievementProducerTest() {
//        AchieveEventDto achieveEventDto = AchieveEventDto.builder()
//                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
//                .userMediaId("U08MRJYTBL1")
//                .achievementId(UUID.fromString("aaaaaaaa-bbbb-cccc-b183-cad3a17bbf53"))
//                .achievementTitle("test Title")
//                .achievementDescription("test description")
//                .build();
//        kafkaTemplate.send("achieve-achievement-test", achieveEventDto);
//
//        try {
//            Map<String, Object> poll = messageQueue.poll(5, TimeUnit.SECONDS);
//            System.out.println("컨슈머 소비 : " + poll);
//            context.sendMessage(poll, new AchievementStrategyImpl() , AchieveEventDto.class);
//            String message = ((MockMessageService) messageService).getMessage();
//            Assertions.assertEquals("축하합니다! test Title 업적을 달성하였습니다!!", message);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }


    private static class MockMessageService implements MessageService {
        String message;
        @Override
        public void sendMessage(String message, String mediaId) {
            System.out.println("Sending message: " + message);
            this.message = message;
        }

        public String getMessage(){
            return this.message;
        }
    }


}
