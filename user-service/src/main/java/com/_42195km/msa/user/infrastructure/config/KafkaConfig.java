package com._42195km.msa.user.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	@Bean
	public <T> ProducerFactory<String, T> producerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		// 추가 튜닝
		props.put(ProducerConfig.ACKS_CONFIG, "all");                     // 모든 ISR ACK 대기
		props.put(ProducerConfig.RETRIES_CONFIG, 5);                      // 재시도 5회
		props.put(ProducerConfig.LINGER_MS_CONFIG, 10);                   // 배치 지연 시간(ms)
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);        // 멱등성 보장

		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	public NewTopic userDeleteTopic() {
		return TopicBuilder.name("delete-user")
			.partitions(3)
			.replicas(3)
			.config("min.insync.replicas", "2")
			.build();
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}
