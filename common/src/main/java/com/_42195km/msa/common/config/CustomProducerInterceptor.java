package com._42195km.msa.common.config;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

public class CustomProducerInterceptor implements ProducerInterceptor<String, String> {

	@Override
	public ProducerRecord<String, String> onSend(ProducerRecord<String, String> producerRecord) {

		Headers headers = producerRecord.headers();

		if (headers.lastHeader("X-User-Id") != null) {
			String token = new String(headers.lastHeader("X-User-Id").value(), StandardCharsets.UTF_8);

			System.out.println("[ProducerInterceptor] Valid Authorization token: " + token);

		} else {
			System.err.println("[ProducerInterceptor] Missing Authorization header!");
		}
		
		return producerRecord;
	}

	@Override
	public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
	}

	@Override
	public void close() {
	}

	@Override
	public void configure(Map<String, ?> map) {
	}
}
