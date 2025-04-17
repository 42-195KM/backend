package com._42195km.msa.rankingservice.domain.model;

import java.time.Duration;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DividePersonal {

	private UUID userId;

	// 거리는 총 거리 (얼마나 많이 뛰었는가?)
	private double totalDistance;

	// 타이머는 총 시간 (얼마나 오래 뛰었는가?)
	private Duration totalTimer;

	// 페이스는 해당 시간동안 얼마나 많이 갔는가?
	private double avgPace;

	@Override
	public String toString() {
		return "DividePersonal{" +
			"userId=" + userId +
			", totalDistance=" + totalDistance +
			", totalTimer=" + totalTimer +
			", avgPace=" + avgPace +
			'}';
	}

}
