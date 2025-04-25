package com._42195km.msa.competitionservice.infrastructure.config;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com._42195km.msa.competitionservice.application.event.CompetitionApplicationEvent;
import com._42195km.msa.competitionservice.application.event.SagaEvent;
import com._42195km.msa.competitionservice.infrastructure.messaging.CompetitionApplyNotificationDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class KafkaConfig {
	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Value("${spring.kafka.consumer.group-id}")
	private String groupId;

	// SagaEvent Producer 설정
	@Bean
	public ProducerFactory<String, SagaEvent> sagaEventProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean("sagaEventKafkaTemplate")
	public KafkaTemplate<String, SagaEvent> sagaEventKafkaTemplate() {
		return new KafkaTemplate<>(sagaEventProducerFactory());
	}

	// SagaEvent Consumer 설정
	@Bean
	public ConsumerFactory<String, SagaEvent> sagaEventConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "_saga");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "com._42195km.msa.competitionservice.application.event");

		return new DefaultKafkaConsumerFactory<>(props,
			new StringDeserializer(),
			new JsonDeserializer<>(SagaEvent.class, false));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SagaEvent> sagaKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, SagaEvent> factory =
			new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(sagaEventConsumerFactory());
		return factory;
	}

	@Bean
	public ProducerFactory<String, CompetitionApplicationEvent> competitionApplicationProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		configProps.put(ProducerConfig.ACKS_CONFIG, "all");
		configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
		log.info("Configuring Kafka producer with bootstrap servers: {}", bootstrapServers);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean("competitionApplicationKafkaTemplate")
	public KafkaTemplate<String, CompetitionApplicationEvent> competitionApplicationKafkaTemplate() {
		KafkaTemplate<String, CompetitionApplicationEvent> template = new KafkaTemplate<>(competitionApplicationProducerFactory());
		log.info("Created KafkaTemplate for CompetitionApplicationEvent");
		return template;
	}

	@Bean
	public ProducerFactory<String, CompetitionApplyNotificationDto> notificationProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean("notificationKafkaTemplate")
	public KafkaTemplate<String, CompetitionApplyNotificationDto> notificationKafkaTemplate() {
		return new KafkaTemplate<>(notificationProducerFactory());
	}

	@Bean
	public ProducerFactory<String, Object> genericProducerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(genericProducerFactory());
	}
}
