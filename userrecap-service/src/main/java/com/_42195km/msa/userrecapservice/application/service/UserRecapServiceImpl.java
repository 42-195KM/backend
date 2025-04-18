package com._42195km.msa.userrecapservice.application.service;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.userrecapservice.application.dto.client.GetRunningRecordAppResponseDto;
import com._42195km.msa.userrecapservice.domain.model.Recap;
import com._42195km.msa.userrecapservice.domain.model.SummaryDetail;
import com._42195km.msa.userrecapservice.domain.model.strategy.SummaryType;
import com._42195km.msa.userrecapservice.domain.repository.UserRecapRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserRecapServiceImpl implements UserRecapService {

	private final UserRecapRepository userRecapRepository;

	@Override
	@Transactional
	public void createUserRecap(SummaryType summaryType, List<GetRunningRecordAppResponseDto> data) {
		List<Double> fieldsToCalculate = data.stream()
			.map(GetRunningRecordAppResponseDto::distance)
			.toList();

		Double totalRepresentativeValue = summaryType.summary(fieldsToCalculate);

		Map<UUID, Double> representativeValueByUser = data.stream()
			.collect(groupingBy(GetRunningRecordAppResponseDto::userId,
				averagingDouble(GetRunningRecordAppResponseDto::distance)));

		List<Double> representativeValueByUserList = representativeValueByUser.values().stream().toList();

		for (Map.Entry<UUID, Double> entry : representativeValueByUser.entrySet()) {
			Double userPercentile = summaryType.calculatePercentile(representativeValueByUserList, entry.getValue());

			Recap recap = Recap.of(
				entry.getKey(),
				SummaryDetail.of(summaryType, totalRepresentativeValue, userPercentile)
			);

			userRecapRepository.save(recap);
		}
	}
}
