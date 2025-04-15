package com._42195km.msa.rankingservice.application.dto.response;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com._42195km.msa.rankingservice.domain.model.DomainType;
import com._42195km.msa.rankingservice.domain.model.Ranking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class GetPersonalRankingResponseDto {

	private UUID id;
	private UUID identifierId;
	private DomainType domainType;
	private List<RankingDetailResponseDto> details;

	public static GetPersonalRankingResponseDto from(Ranking ranking) {
		return GetPersonalRankingResponseDto.builder()
			.id(ranking.getId())
			.identifierId(ranking.getIdentifierId())
			.domainType(ranking.getDomainType())
			.details(ranking.getDetails().stream()
				.map(RankingDetailResponseDto::from)
				.collect(Collectors.toList()))
			.build();
	}
}
