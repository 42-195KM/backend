package com._42195km.alertservice;

import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.in.AchievementStrategyImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.retry.annotation.Backoff;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Import(RetryKafkaConsumerTest.TestConfig.class)
@DirtiesContext
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class RetryKafkaConsumerTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private AlertContext context;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ObjectMapper objectMapper;
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


    @KafkaListener(topics = "retry-test", groupId = "retry-group-test")
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000), // 초기 간격은 1초로 재시도 간격이 2배씩 증가
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dead-t" // 기존 토픽 이름에서 -dead-t 접미사가 추가된 이름으로 Dead Letter Topic 생성
    )
    void achievementConsumer(Map<String, Object> eventMap) {
        System.out.println("=================> Consumer 호출");

        AchieveEventDto achieveEventDto = objectMapper.convertValue(eventMap, AchieveEventDto.class);
        if(achieveEventDto.getAchievementTitle().equals("retry-test")) {
            throw new RuntimeException("retry-test");
        }
        messageQueue.offer(eventMap);
    }

    @KafkaListener(topics = "retry-test-dead-t", groupId = "retry-group-test")
    void achievementConsumerDead(Map<String, Object> eventMap) {
        System.out.println("Dead Letter Topic 메시지 도착: " + eventMap);
        messageQueue.offer(eventMap); // 실패 메시지를 messageQueue에 넣어줌
    }


    @Test
    @DisplayName("업적 메시지 전송 테스트")
    void retryDLTConsumerTest() {
        AchieveEventDto achieveEventDto = AchieveEventDto.builder()
                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .userMediaId("U08MRJYTBL1")
                .achievementId(UUID.fromString("aaaaaaaa-bbbb-cccc-b183-cad3a17bbf53"))
                .achievementTitle("retry-test")
                .achievementDescription("test description")
                .build();
        kafkaTemplate.send("retry-test", achieveEventDto);

        try {
            Map<String, Object> poll = messageQueue.poll(10, TimeUnit.SECONDS);
            AchieveEventDto pollDto = objectMapper.convertValue(poll, AchieveEventDto.class);
            System.out.println("컨슈머 소비 : " + poll);

            Assertions.assertThat(pollDto).usingRecursiveComparison().isEqualTo(achieveEventDto);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


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
