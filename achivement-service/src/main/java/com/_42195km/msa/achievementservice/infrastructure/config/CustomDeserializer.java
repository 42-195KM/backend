package com._42195km.msa.achievementservice.infrastructure.config;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomDeserializer implements Deserializer<Object> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Object deserialize(String s, byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		try {
			return objectMapper.readValue(bytes, Object.class);
		}
		catch (Exception e) {
			throw new SerializationException("Failed to deserialize object", e);
		}
	}
}
