package com._42195km.msa.userrecapservice.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.domain.model.DataFormat;
import com._42195km.msa.userrecapservice.domain.model.DataFormatRow;
import com._42195km.msa.userrecapservice.domain.model.Recap;
import com._42195km.msa.userrecapservice.domain.model.SummaryDetail;
import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;
import com._42195km.msa.userrecapservice.domain.repository.UserRecapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRecapServiceImpl implements UserRecapService {

	private static final Map<SummaryType, Function<GetRunningRecordAppResponseDto, Double>> SUMMARY_FILED_MAP = Map.of(
		SummaryType.MONTHLY_CUMULATIVE_DISTANCE, GetRunningRecordAppResponseDto::distance,
		SummaryType.MONTHLY_AVERAGE_PACE, GetRunningRecordAppResponseDto::pace
	);

	private final UserRecapRepository userRecapRepository;

	@Override
	@Transactional
	public void createUserRecap(SummaryType summaryType, List<GetRunningRecordAppResponseDto> data) {

		List<DataFormatRow<Double>> rows = data.stream()
			.map(dto -> new DataFormatRow<>(dto.userId(), SUMMARY_FILED_MAP.get(summaryType).apply(dto), summaryType))
			.toList();

		DataFormat<Double> df = DataFormat.of(rows);

		Double statistics = summaryType.summary(df);

		Map<UUID, Double> dfGroupByUser = df.getAggregatedValuesByUser(
			DataFormatRow::value,
			summaryType.getMetricCollector()
		);

		List<Double> distribution = dfGroupByUser.values().stream()
			.toList();

		for (Map.Entry<UUID, Double> entry : dfGroupByUser.entrySet()) {
			Double userPercentile = summaryType.calculatePercentile(distribution, entry.getValue());

			Recap recap = Recap.of(
				entry.getKey(),
				SummaryDetail.of(
					summaryType,
					statistics,
					userPercentile
				)
			);

			userRecapRepository.save(recap);
		}
	}
}
