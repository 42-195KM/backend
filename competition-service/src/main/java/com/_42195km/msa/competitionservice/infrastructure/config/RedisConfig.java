package com._42195km.msa.competitionservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com._42195km.msa.competitionservice.domain.model.SagaState;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 알 수 없는 속성 무시

		return mapper;
	}

	@Bean
	public RedisTemplate<String, SagaState> sagaRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
		RedisTemplate<String, SagaState> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());

		Jackson2JsonRedisSerializer<SagaState> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, SagaState.class);

		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);

		return template;
	}
}
