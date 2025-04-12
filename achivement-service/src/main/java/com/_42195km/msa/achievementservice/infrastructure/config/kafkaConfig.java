package com._42195km.msa.achievementservice.infrastructure.config;

import static org.apache.kafka.streams.kstream.EmitStrategy.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;
// import com.fasterxml.jackson.databind.JsonDeserializer;

@Configuration
@EnableKafka
public class kafkaConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

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
		configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomDeserializer.class);
		// 모든 패키지의 클래스 역직렬화 허용 (보안에 주의)
		configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		// 타입 정보가 없는 경우 Map으로 변환
		// configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "java.util.Map");

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

		// // 동시에 처리할 수 있는 스레드 수 (파티션 수에 맞게 조정)
		// factory.setConcurrency(3);
		// // 배치 리스너 비활성화 (개별 메시지 처리)
		// //factory.setBatchListener(false);
		//
		// 에러 핸들러 수정 - 재시도 없이 로깅만 하도록 설정
		DefaultErrorHandler errorHandler = new DefaultErrorHandler((record, exception) -> {
			log.error("메시지 처리 실패: {}", exception.getMessage());
			log.error("실패한 메시지: {}", record);
			// 필요한 추가 작업 수행 (예: 데드 레터 큐로 전송)
		}, new FixedBackOff(3L, 3L)); // 재시도 3회 반복

		// SerializationException도 처리하도록 설정
		errorHandler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);

		//
		factory.setCommonErrorHandler(errorHandler);

		return factory;
	}
}
