package com._42195km.msa.common.config;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventSender {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void send(String topic, Object message) {
		sendWithUserIdHeader(topic, message);
	}

	private void sendWithUserIdHeader(String topic, Object message) {
		String header = extractUserIdFromHeader();

		log.info("sendWithUserIdHeader: token={}", header);

		ProducerRecord<String, Object> record = new ProducerRecord<>(topic, message);
		if (header != null) {
			record.headers().add("X-User-Id", header.getBytes(StandardCharsets.UTF_8));
		}

		kafkaTemplate.send(record);
	}

	private String extractUserIdFromHeader() {
		ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attrs != null) {
			HttpServletRequest request = attrs.getRequest();
			return request.getHeader("X-User-Id");
		}
		return null;
	}

}
