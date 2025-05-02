package com._42195km.msa.userrecapservice.infrastructure.client.feign;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.application.service.client.RunningRecordClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeignRunningRecordClientImpl implements RunningRecordClient {

	private final FeignRunningRecordClient feignRunningRecordClient;
	private List<GetRunningRecordAppResponseDto> data = new ArrayList<>();

	@Override
	public List<GetRunningRecordAppResponseDto> findAllRunningRecords(
		LocalDateTime startDate, LocalDateTime endDate
	) {
		List<UUID> userIds = new ArrayList<>();

		for (int i = 1; i <= 1500; i++) {
			userIds.add(UUID.randomUUID());
		}

		if (data.isEmpty()) {
			data.addAll(TestRunningRecordGenerator.generateRunningRecords(userIds, 2025, 3));
		}

		return data;
	}

	static class TestRunningRecordGenerator {
		public static List<GetRunningRecordAppResponseDto> generateRunningRecords(
			List<UUID> userIds, int year, int month
		) {
			List<GetRunningRecordAppResponseDto> records = new ArrayList<>();
			LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
			LocalDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());

			for (UUID userId : userIds) {
				LocalDateTime currentDate = startDate;
				while (!currentDate.isAfter(endDate)) {
					records.add(GetRunningRecordAppResponseDto.builder()
						.id(UUID.randomUUID())
						.userId(userId)
						.distance(generateDistance())
						.pace(generatePace())
						.duration(generateDuration())
						.createdAt(currentDate.withHour(generateHour()).withMinute(generateMinute()).withSecond(0))
						.build());
					currentDate = currentDate.plusDays(1);
				}
			}
			return records;
		}

		private static double generateDistance() {
			return Math.round((Math.random() * 7 + 3) * 10.0) / 10.0; // 3.0 ~ 10.0 km 사이
		}

		private static double generatePace() {
			return Math.round((Math.random() * 2 + 4) * 10.0) / 10.0; // 4.0 ~ 6.0 분/km 사이
		}

		private static Duration generateDuration() {
			double minutes = generateDistance() * generatePace();
			return Duration.ofMinutes((long)minutes);
		}

		private static int generateHour() {
			return (int)(Math.random() * 24);
		}

		private static int generateMinute() {
			return (int)(Math.random() * 60);
		}
	}
}
