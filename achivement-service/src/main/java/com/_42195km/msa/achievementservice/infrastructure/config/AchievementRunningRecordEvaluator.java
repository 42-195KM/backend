package com._42195km.msa.achievementservice.infrastructure.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import com._42195km.msa.achievementservice.application.dto.response.RunningRecordEventDto;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.domain.model.AchievementUser;
import com._42195km.msa.achievementservice.domain.model.CriteriaInequality;

public class AchievementRunningRecordEvaluator {

	// criteria 값과 해당 필드의 값을 추출하는 함수를 매핑한 Map을 정의
	private static final Map<String, Function<RunningRecordEventDto, Double>> criteriaExtractor = new HashMap<>();

	static {
		// double 값은 그대로 반환
		criteriaExtractor.put("distance", RunningRecordEventDto::getDistance);
		criteriaExtractor.put("pace", RunningRecordEventDto::getPace);
		criteriaExtractor.put("totalDistance", RunningRecordEventDto::getTotalDistance);
		criteriaExtractor.put("avgPace", RunningRecordEventDto::getAvgPace);

		// Duration 타입의 필드는 Duration을 초 단위의 double 값으로 변환
		criteriaExtractor.put("timer", dto -> {
			Duration duration = dto.getTimer();
			return (duration != null) ? duration.toMillis() / 1000.0 : 0.0;
		});
		criteriaExtractor.put("totalDuration", dto -> {
			Duration duration = dto.getTotalDuration();
			return (duration != null) ? duration.toMillis() / 1000.0 : 0.0;
		});
	}

	public boolean isAlreadyAchieved(RunningRecordEventDto eventDto, Achievement achievement) {
		Set<UUID> achievementUsers = achievement.getAchievementUsers().stream()
			.map(achievementUser -> achievementUser.getUserId())
			.collect(Collectors.toSet());

		return achievementUsers.contains(eventDto.getUserId());
	}

	public boolean isAchievementMet(RunningRecordEventDto eventDto, Achievement achievement) {
		String criteria = achievement.getCriteria(); // 예: "distance", "timer", "pace" 등
		double getCriteriaValue = achievement.getCriteriaValue(); // 업적 기준 값 (double)
		CriteriaInequality criteriaInequality = achievement.getCriteriaInequality(); // 업적 기준 부등호

		Function<RunningRecordEventDto, Double> extractor = criteriaExtractor.get(criteria);
		if (extractor == null) {
			throw new IllegalArgumentException("정의되지 않은 criteria: " + criteria);
		}
		double value = extractor.apply(eventDto);

		// 기준 부등호에 따라 비교
		switch (criteriaInequality) {
			case LESS_THAN:
				return value < getCriteriaValue;
			case LESS_THAN_OR_EQUAL_TO:
				return value <= getCriteriaValue;
			case EQUAL:
				// double 비교의 경우, 엄격한 비교 대신 오차 범위를 고려할 수 있다.
				return Math.abs(value - getCriteriaValue) < 1e-6;
			case NOT_EQUAL:
				return Math.abs(value - getCriteriaValue) >= 1e-6;
			case MORE_THAN_OR_EQUAL_TO:
				return value >= getCriteriaValue;
			case MORE_THAN:
				return value > getCriteriaValue;
			default:
				throw new IllegalStateException("알 수 없는 CriteriaInequality: " + criteriaInequality);
		}
	}
}
