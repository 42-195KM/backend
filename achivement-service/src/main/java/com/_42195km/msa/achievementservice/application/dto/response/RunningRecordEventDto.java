package com._42195km.msa.achievementservice.application.dto.response;

import java.time.Duration;
import java.util.UUID;

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

	private double totalDistance;
	private Duration totalDuration;
	private double avgPace;
}
