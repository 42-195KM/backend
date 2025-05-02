package com._42195km.msa.userrecapservice.domain.model.strategy;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com._42195km.msa.userrecapservice.domain.model.DataFormat;
import com._42195km.msa.userrecapservice.domain.model.DataFormatRow;

import lombok.Getter;

@Getter
public enum SummaryType implements SummaryStrategy<Double> {
	MONTHLY_CUMULATIVE_DISTANCE("누적 거리", Collectors.summingDouble(v -> v)) {
		@Override
		public Double summary(DataFormat<Double> df) {
			double sum = df.getRows().stream()
				.mapToDouble(DataFormatRow::value)
				.summaryStatistics()
				.getSum();

			int count = df.getRows().stream()
				.collect(Collectors.groupingBy(DataFormatRow::userId))
				.size();

			return sum / count;
		}

		@Override
		public Double calculatePercentile(List<Double> distribution, Double userValue) {
			long upperCount = distribution.stream()
				.filter(d -> d > userValue)
				.count();

			return (double)upperCount / distribution.size() * 100;
		}
	},

	MONTHLY_AVERAGE_PACE("평균 페이스", Collectors.averagingDouble(v -> v)) {
		@Override
		public Double summary(DataFormat<Double> df) {
			Map<UUID, Double> averagePaceByUser = df.getAggregatedValuesByUser(
				DataFormatRow::value,
				this.getMetricCollector()
			);

			return averagePaceByUser.values().stream()
				.mapToDouble(v -> v)
				.average()
				.orElseGet(() -> 0.0);
		}

		@Override
		public Double calculatePercentile(List<Double> distribution, Double userValue) {
			long upperCount = distribution.stream()
				.filter(d -> d > userValue)
				.count();

			return (double)upperCount / distribution.size() * 100;
		}
	};

	private final String name;
	private final Collector<Double, ?, Double> metricCollector;

	SummaryType(String name, Collector<Double, ?, Double> metricCollector) {
		this.name = name;
		this.metricCollector = metricCollector;
	}
}
