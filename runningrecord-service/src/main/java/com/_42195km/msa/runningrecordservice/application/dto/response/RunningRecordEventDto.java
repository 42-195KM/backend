package com._42195km.msa.runningrecordservice.application.dto.response;

import java.time.Duration;
import java.util.UUID;

import com._42195km.msa.runningrecordservice.domain.model.RunningRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RunningRecordEventDto {
	private UUID id;
	private UUID userId;
	private double distance;
	private Duration timer;
	private double pace;

	private double totalDistance = 0;
	private Duration totalDuration = Duration.ZERO;
	private double avgPace = 0;

	public static RunningRecordEventDto from(RunningRecord runningRecord) {
		return RunningRecordEventDto.builder()
			.id(runningRecord.getId())
			.userId(runningRecord.getUserId())
			.distance(runningRecord.getDistance())
			.timer(runningRecord.getTimer())
			.pace(runningRecord.getPace())
			.build();
	}
}
