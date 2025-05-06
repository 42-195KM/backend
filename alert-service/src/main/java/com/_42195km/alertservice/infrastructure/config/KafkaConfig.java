package com._42195km.alertservice.infrastructure.config;

import com._42195km.msa.common.config.CustomDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;


    /**
     * 공통 Producer 설정: 이벤트 타입에 관계없이 Object로 처리
     * @return
     */
    @Bean
    public <T> ProducerFactory<String, T> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * 공통 Consumer 설정: Object 타입으로 수신하고, 각 서비스에서 원하는 타입으로 캐스팅/변환
     * @return
     */
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomDeserializer.class);
        // 모든 패키지의 클래스 역직렬화 허용 (보안에 주의)
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        // 타입 정보가 없는 경우 Map으로 변환
        // configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.Map");

        // 성능 최적화 설정
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1024);// 최소 1KB 데이터를 가져올 때까지 대기
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);// 최대 500ms 대기
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);// 한 번에 최대 500개 레코드 가져오기

        // 리밸런싱 최적화
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);// 세션 타임아웃 (30초)
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000);// 하트비트 간격 (10초)
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);// 최대 폴링 간격 (5분)

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    /**
     * 공통 kafka template
     * @return
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * 공통 kafka listener container factory
     * @return
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());

         // 동시에 처리할 수 있는 스레드 수 (파티션 수에 맞게 조정)
         factory.setConcurrency(3);
         // 배치 리스너 비활성화 (개별 메시지 처리)
         factory.setBatchListener(true);

        // DLT 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate(), // DLT로 보낼 때 사용할 KafkaTemplate
                (record, ex) -> {
                    log.error("메시지 처리 실패: {}", ex.getMessage());
                    log.error("실패한 메시지: {}", record);
                    // 실패한 메시지를 보낼 토픽을 지정. 기본은 "<원본토픽>.-dead-t"
                    String dltTopic = record.topic() + "-dead-t";
                    return new TopicPartition(dltTopic, record.partition());
                }
        );

        // 에러 핸들러 수정 - 재시도 없이 로깅만 하도록 설정
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L)); // 재시도 3회 반복

        // SerializationException도 처리하도록 설정
        errorHandler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);

        //
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }

}
