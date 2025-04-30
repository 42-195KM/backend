package com._42195km.msa.achievementservice.infrastructure.evaluator;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com._42195km.msa.achievementservice.infrastructure.messaging.in.RunningRecordEventDto;

import lombok.Getter;

public enum AchievementRunningRecordCriteriaField {
	DISTANCE("distance", RunningRecordEventDto::getDistance),
	PACE("pace", RunningRecordEventDto::getPace),
	TOTAL_DISTANCE("total_distance", RunningRecordEventDto::getTotalDistance),
	AVG_PACE("avgPace", RunningRecordEventDto::getAvgPace),
	TIMER("timer", dto -> toSeconds(dto.getTimer())),
	TOTAL_DURATION("total_timer", dto -> toSeconds(dto.getTotalTimer()));

	@Getter
	private final String key;
	private final Function<RunningRecordEventDto, Double> extractor;

	private static final Map<String, AchievementRunningRecordCriteriaField> criteriaFieldMap =
		Stream.of(values()).collect(Collectors.toMap(AchievementRunningRecordCriteriaField::getKey, cf -> cf));

	AchievementRunningRecordCriteriaField(String key, Function<RunningRecordEventDto, Double> extractor) {
		this.key = key;
		this.extractor = extractor;
	}

	public double extract(RunningRecordEventDto dto) {
		return extractor.apply(dto);
	}

	public static AchievementRunningRecordCriteriaField fromKey(String key) {
		AchievementRunningRecordCriteriaField field = criteriaFieldMap.get(key);
		if (field == null) {
			throw new IllegalArgumentException("정의되지 않은 criteria: " + key);
		}
		return field;
	}

	private static double toSeconds(Duration d) {
		return (d == null) ? 0.0 : d.toMillis() / 1000.0;
	}
}
