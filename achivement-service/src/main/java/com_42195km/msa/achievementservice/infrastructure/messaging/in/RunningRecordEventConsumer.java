package com_42195km.msa.achievementservice.infrastructure.messaging.in;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import com_42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com_42195km.msa.achievementservice.domain.repository.AchievementUserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RunningRecordEventConsumer {

	private final ObjectMapper objectMapper;
	private final AchievementRepository achievementRepository;
	private final AchievementUserRepository achievementUserRepository;

	@KafkaListener(topics = "create-running-record", groupId = "achievement-group")
	public void handleRunningRecordCreateEvent(Map<String, Object> eventMap) {
		for(Map.Entry<String, Object> entry : eventMap.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			System.out.println("key: " + key + " value: " + value);
		}
	}

	/**
	 * user_id	UUID	사용자 id
	 * distance	DECIMAL(10, 2)	거리
	 * timer	Duration	시간
	 * pace	DECIMAL(10, 2)	평균 페이스
	 */
}
