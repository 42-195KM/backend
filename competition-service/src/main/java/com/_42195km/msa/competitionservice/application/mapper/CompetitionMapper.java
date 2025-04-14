package com._42195km.msa.competitionservice.application.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.presentation.dto.response.CompetitionResponseDto;

@Component
public class CompetitionMapper {

	/**
	 * 엔티티 -> AppDTO
	 * @param competition
	 * @return
	 */
	public CompetitionAppResponseDto toAppResponseDto(Competition competition) {
		return new CompetitionAppResponseDto(
			competition.getId(),
			competition.getUserId(),
			competition.getTitle(),
			competition.getType(),
			competition.getReceptionType(),
			competition.getParticipantsNum(),
			competition.getPrice()
		);
	}

	/**
	 * 엔티티 Page -> AppDTO Page
	 * @param competitions
	 * @return
	 */
	public Page<CompetitionAppResponseDto> toAppResponseDtoPage(Page<Competition> competitions) {
		return competitions.map(competition -> toAppResponseDto(competition)); //this::toAppResponseDto
	}

	/**
	 * AppDTO -> PresentationDTO
	 * @param appResponseDto
	 * @return
	 */
	public CompetitionResponseDto toPresentationDto(CompetitionAppResponseDto appResponseDto) {
		return new CompetitionResponseDto(
			appResponseDto.getId(),
			appResponseDto.getUserId(),
			appResponseDto.getTitle(),
			appResponseDto.getType(),
			appResponseDto.getReceptionType(),
			appResponseDto.getParticipantsNum(),
			appResponseDto.getPrice()
		);
	}

	/**
	 * AppDTO Page -> Presentation DTO Page
	 * @param appResponseDtos
	 * @return
	 */
	public Page<CompetitionResponseDto> toPresentationDtoPage(Page<CompetitionAppResponseDto> appResponseDtos) {
		return appResponseDtos.map(competition -> toPresentationDto(competition));
	}
}
