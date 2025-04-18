package com._42195km.msa.userrecapservice.domain.model;

import java.util.List;

import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class SummaryDetail {

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "statistics", nullable = false)
	private Double statistics;

	@Column(name = "user_percentile", nullable = false)
	private Double userPercentile;

	private SummaryDetail(String name, Double statistics, Double userPercentile) {
		validateName(name);

		this.name = name;
		this.statistics = statistics;
		this.userPercentile = userPercentile;
	}

	public static SummaryDetail of(SummaryType summaryType, Double statistics, Double userPercentile) {
		return new SummaryDetail(
			summaryType.getName(),
			statistics,
			userPercentile
		);
	}

	private void validateName(String name) {
		if (name.isEmpty() || name.isBlank()) {
			throw new IllegalArgumentException("요약 정보 제목은 비워둘 수 없습니다.");
		}
	}
}
