package com._42195km.msa.runningrecordservice.infrastructure.config;

import static org.apache.kafka.streams.kstream.EmitStrategy.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import com._42195km.msa.common.config.CustomDeserializer;

@Configuration
@EnableKafka
public class KafkaConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String kafkaBootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	/**
	 * Producer 설정: 이벤트 타입에 관계없이 Object로 처리
	 * @return
	 */
	@Bean
	public <T> ProducerFactory<String, T> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		// 추가 튜닝
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");                     // 모든 ISR ACK 대기
		configProps.put(ProducerConfig.RETRIES_CONFIG, 5);                      // 재시도 5회
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);                   // 배치 지연 시간(ms)
		configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);        // 멱등성 보장

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	/**
	 * create-running-record 토픽 생성:
	 * 토픽을 3개의 파티션으로 쪼개서 생성,
	 * 클러스터 내 서로 다른 3개의 브로커에 복제본 생성,
	 * “in-sync replica(ISR)”로 간주되어야 할 최소 복제본 수는 2로 설정
	 * @return
	 */
	@Bean
	public NewTopic RunningRecordTopic() {
		return TopicBuilder.name("create-running-record")
			.partitions(3)
			.replicas(3)
			.config("min.insync.replicas", "2")
			.build();
	}

	/**
	 * Consumer 설정: Object 타입으로 수신하고, 각 서비스에서 원하는 타입으로 캐스팅/변환
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

		// 추가 튜닝
		configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);         // 수동 오프셋 커밋
		configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");    // 토픽 없으면 처음부터
		configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);           // 폴링당 최대 레코드 수
		configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);       // 세션 타임아웃(ms)

		return new DefaultKafkaConsumerFactory<>(configProps);
	}

	/**
	 * kafka template
	 * @return
	 */
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	/**
	 * kafka listener container factory
	 * @return
	 */
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		return factory;
	}
}
