package com._42195km.msa.achievementservice;

import com._42195km.msa.achievementservice.infrastructure.messaging.out.AchieveEventDto;
import com._42195km.msa.achievementservice.infrastructure.messaging.out.AchieveEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

@SpringBootTest
public class AchievementProducerTest {

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



}
