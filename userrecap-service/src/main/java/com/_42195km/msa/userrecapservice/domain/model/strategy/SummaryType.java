package com._42195km.msa.userrecapservice.domain.model.strategy;

import java.util.List;
import java.util.function.ToDoubleFunction;

import lombok.Getter;

@Getter
public enum SummaryType implements SummaryStrategy {
	MONTHLY_CUMULATIVE_DISTANCE("누적 거리") {
		@Override
		public Double summary(List<Double> data) {
			return data.parallelStream()
				.mapToDouble(Double::doubleValue)
				.summaryStatistics()
				.getAverage();
		}

		@Override
		public Double calculatePercentile(List<Double> data, Double userValue) {
			long upperCount = data.stream()
				.filter(d -> d >= userValue)
				.count();

			return (double)upperCount / data.size() * 100;
		}
	};

	private final String name;

	SummaryType(String name) {
		this.name = name;
	}

}
