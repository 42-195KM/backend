package com._42195km.alertservice;

import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.in.AchievementStrategyImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Import(RetryKafkaConsumerTest.TestConfig.class)
@DirtiesContext
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"})
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class RetryKafkaConsumerTest {
    private static final Logger log = LoggerFactory.getLogger(RetryKafkaConsumerTest.class);
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private static final LinkedBlockingQueue<Map<String,Object>> messageQueue = new LinkedBlockingQueue<>();

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


    @KafkaListener(concurrency = "1", topics = "retry-test", groupId = "retry-group-test")
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000), //  1초
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dead-t" // 기존 토픽 이름에서 -dead-t 접미사가 추가된 이름으로 Dead Letter Topic 생성
    )
    void achievementConsumer(Map<String, Object> eventMap) {
        log.info("=================> 컨슈머 호출");

        throw new RuntimeException("retry-test");

    }

    @KafkaListener(concurrency = "1", topics = "retry-test-dead-t", groupId = "retry-group-test")
    void achievementConsumerDead(Map<String, Object> eventMap) {
        log.info("DLT 컨슈머 {}: " , eventMap);
        messageQueue.offer(eventMap); // 실패 메시지를 messageQueue에 넣어줌
        AchieveEventDto pollDto = objectMapper.convertValue(eventMap, AchieveEventDto.class);
        Assertions.assertEquals(pollDto.getAchievementDescription(), "test description");
    }

    @Test
    @DisplayName("DLT 테스트")
    void retryDLTConsumerTest() {
        AchieveEventDto achieveEventDto = AchieveEventDto.builder()
                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .userMediaId("U08MRJYTBL1")
                .achievementId(UUID.fromString("aaaaaaaa-bbbb-cccc-b183-cad3a17bbf53"))
                .achievementTitle("retry-test")
                .achievementDescription("test description")
                .build();
        kafkaTemplate.send("retry-test", achieveEventDto);

//        try {
//            Map<String, Object> poll = messageQueue.poll(20, TimeUnit.SECONDS);
//            AchieveEventDto pollDto = objectMapper.convertValue(poll, AchieveEventDto.class);
//            log.info("컨슈머 소비 : " + poll);
//            Assertions.assertThat(pollDto).usingRecursiveComparison().isEqualTo(achieveEventDto);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
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
