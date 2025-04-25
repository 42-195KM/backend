package com._42195km.alertservice;

import com._42195km.alertservice.application.service.AlertContext;
import com._42195km.alertservice.application.service.MessageService;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;
import com._42195km.alertservice.infrastructure.messaging.in.AchievementStrategyImpl;
import com._42195km.alertservice.infrastructure.messaging.in.CompetitionStrategyImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
@Import(KafkaConsumerSendAlertTest.TestConfig.class)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"})
@EmbeddedKafka(
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class KafkaConsumerSendAlertTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerSendAlertTest.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private AlertContext context;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ObjectMapper objectMapper;
    private static final LinkedBlockingQueue<Map<String,Object>> messageQueue = new LinkedBlockingQueue<>();

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

    @BeforeEach
    void setUp() {
        messageQueue.clear();
    }



    @Test
    @Order(1)
    @DisplayName("대회 메시지 전송 테스트")
    void CompetitionProducerTest() {
        CompetitionEventDto competitionEventDto = CompetitionEventDto.builder()
                .userId(UUID.fromString("caaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .mediaId("U08MRJYTBL1")
                .title("test Title 대회")
                .build();
        kafkaTemplate.send("competition_notification-test", competitionEventDto);

//        try {
//            Map<String, Object> poll = messageQueue.poll(5, TimeUnit.SECONDS);
//            log.info("대회 컨슈머 소비 : " + poll);
//
//            context.sendMessage(poll, new CompetitionStrategyImpl() , CompetitionEventDto.class);
//            String message = ((MockMessageService) messageService).getMessage();
//            Assertions.assertEquals("test Title 대회 신청이 완료되었습니다.", message);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
    @KafkaListener(concurrency = "1", topics = "competition_notification-test", groupId = "competition.group.id")
    void competitionConsumer(Map<String, Object> eventMap) {
        log.info("========대회 컨슈머: {}", eventMap);
//        messageQueue.offer(eventMap);
        context.sendMessage(eventMap, new CompetitionStrategyImpl() , CompetitionEventDto.class);
        String message = ((MockMessageService) messageService).getMessage();
        Assertions.assertEquals("test Title 대회 신청이 완료되었습니다.", message);
    }


    @Test
    @Order(2)
    @DisplayName("업적 메시지 전송 테스트")
    void AchievementProducerTest() {
        AchieveEventDto achieveEventDto = AchieveEventDto.builder()
                .userId(UUID.fromString("acaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .userMediaId("U08MRJYTBL1")
                .achievementId(UUID.fromString("acaaaaaa-bbbb-cccc-b183-cad3a17bbf53"))
                .achievementTitle("test Title")
                .achievementDescription("test description")
                .build();
        kafkaTemplate.send("achieve-achievement-test", achieveEventDto);

//        try {
//            Map<String, Object> poll = messageQueue.poll(5, TimeUnit.SECONDS);
//            log.info("업적 컨슈머 소비 : " + poll);
//
//            context.sendMessage(poll, new AchievementStrategyImpl() , AchieveEventDto.class);
//            String message = ((MockMessageService) messageService).getMessage();
//            Assertions.assertEquals("축하합니다! test Title 업적을 달성하였습니다!!", message);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
    @KafkaListener(concurrency = "1", topics = "achieve-achievement-test", groupId = "achieve.group.id")
    void achievementConsumer(Map<String, Object> eventMap) {
        log.info("=========업적 컨슈머: {}", eventMap);
//        messageQueue.offer(eventMap);
        context.sendMessage(eventMap, new AchievementStrategyImpl() , AchieveEventDto.class);
        String message = ((MockMessageService) messageService).getMessage();
        Assertions.assertEquals("축하합니다! test Title 업적을 달성하였습니다!!", message);
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
