package com._42195km.alertservice;

import com._42195km.alertservice.infrastructure.messaging.in.AchieveEventDto;
import com._42195km.alertservice.infrastructure.messaging.in.CompetitionEventDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.UUID;

@SpringBootTest
//@EmbeddedKafka(partitions = 1,
//        brokerProperties = {"listeners=PLAINTEXT://localhost:9092"},
//        ports = { 9092 }
//)
public class KafkaConsumerTest {



    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;



    @Test
    void AchievementProducerTest() {


        AchieveEventDto achieveEventDto = AchieveEventDto.builder()
                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .userMediaId("U08MRJYTBL1")
                .achievementId(UUID.fromString("aaaaaaaa-bbbb-cccc-b183-cad3a17bbf53"))
                .achievementTitle("test Title")
                .achievementDescription("test description")
                .build();

        kafkaTemplate.send("achieve-achievement", achieveEventDto);


    }

    @Test
    void CompetitionProducerTest() {


        CompetitionEventDto competitionEventDto = CompetitionEventDto.builder()
                .userId(UUID.fromString("aaaaaaaa-0bda-11f0-b183-cad3a17bbf53"))
                .mediaId("U08MRJYTBL1")
                .title("test Title 대회")
                .build();

        kafkaTemplate.send("competition_notification", competitionEventDto);


    }



}
