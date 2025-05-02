package com._42195km.msa.userrecapservice.domain.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public class DataFormat<T> {
	private final List<DataFormatRow<T>> rows;

	private DataFormat(List<DataFormatRow<T>> rows) {
		this.rows = rows;
	}

	public static <T> DataFormat<T> of(List<DataFormatRow<T>> rows) {
		return new DataFormat<>(rows);
	}

	public <A, D, U> Map<UUID, D> getAggregatedValuesByUser(
		Function<DataFormatRow<T>, U> extractor,
		Collector<U, A, D> downStreamCollector
	) {
		return rows.stream()
			.collect(Collectors.groupingBy(
				DataFormatRow::userId,
				Collectors.mapping(extractor, downStreamCollector)
			));
	}
}
