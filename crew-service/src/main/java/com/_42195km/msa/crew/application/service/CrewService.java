package com._42195km.msa.crew.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.crew.application.dto.request.CreateCrewAppRequestDto;
import com._42195km.msa.crew.application.dto.response.CreateCrewAppResponseDto;
import com._42195km.msa.crew.application.exception.CrewBusinessException;
import com._42195km.msa.crew.application.exception.CrewServiceCode;
import com._42195km.msa.crew.domain.model.Crew;
import com._42195km.msa.crew.domain.repository.CrewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrewService {
	private final CrewRepository crewRepository;

	@Transactional
	public CreateCrewAppResponseDto createCrew(CreateCrewAppRequestDto dto) {
		if (crewRepository.existsByName(dto.name())) {
			throw new CrewBusinessException(CrewServiceCode.CREW_NAME_DUPLICATED);
		}

		Crew crew = Crew.builder()
			.name(dto.name())
			.description(dto.description())
			.capacity(dto.capacity())
			.isAutoAgree(dto.isAutoAgree())
			.build();

		crewRepository.save(crew);

		return new CreateCrewAppResponseDto(
			crew.getId(),
			crew.getName(),
			crew.getDescription(),
			crew.getCaptainId(),
			crew.getCapacity(),
			crew.getIsAutoAgree()
		);
	}
}
