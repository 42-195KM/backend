package com._42195km.msa.rankingservice.application.dto.response;

import java.time.LocalDate;
import java.util.UUID;

import com._42195km.msa.rankingservice.domain.model.RankingDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RankingDetailResponseDto {
	private UUID id;
	private String metricName;
	private String metricValue;
	private int rank;
	private LocalDate date;

	public static RankingDetailResponseDto from(RankingDetail detail) {
		return RankingDetailResponseDto.builder()
			.id(detail.getId())
			.metricName(detail.getMetricName())
			.metricValue(detail.getMetricValue())
			.rank(detail.getRank())
			.date(detail.getDate())
			.build();
	}
}
