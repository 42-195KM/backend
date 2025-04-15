package com._42195km.msa.crew.application.service;

import static com._42195km.msa.crew.domain.model.CrewMemberMapping.CrewMemberStatus.*;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.crew.application.dto.request.CreateCrewAppRequestDto;
import com._42195km.msa.crew.application.dto.request.UpdateCrewAppRequestDto;
import com._42195km.msa.crew.application.dto.response.CreateCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.GetSpecificCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.JoinCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.SearchCrewAppPagingResponseDto;
import com._42195km.msa.crew.application.dto.response.UpdateCrewAppResponseDto;
import com._42195km.msa.crew.application.exception.CrewBusinessException;
import com._42195km.msa.crew.application.exception.CrewServiceCode;
import com._42195km.msa.crew.domain.model.Crew;
import com._42195km.msa.crew.domain.model.CrewMember;
import com._42195km.msa.crew.domain.model.CrewMemberMapping;
import com._42195km.msa.crew.domain.repository.CrewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrewService {
	private final CrewRepository crewRepository;

	@Transactional
	public CreateCrewAppResponseDto createCrew(CreateCrewAppRequestDto dto) {
		if (crewRepository.existsByName(dto.name())) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_NAME_DUPLICATED);
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

	public JoinCrewAppResponseDto applyJoiningCrew(UUID crewId, UUID userId) {
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isFull()) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_IS_FULL);
		}

		if (crew.isAlreadyJoined(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_MEMBER_ALREADY_JOINED);
		}

		CrewMember crewMember = CrewMember.builder()
			.userId(null)
			.build();

		CrewMemberMapping crewMemberMapping = CrewMemberMapping.builder()
			.crew(crew)
			.crewMember(crewMember)
			.status(crew.getIsAutoAgree() ? APPROVED : PENDING)
			.build();

		crew.addCrewMemberMapping(crewMemberMapping);
		crewRepository.save(crew);

		return new JoinCrewAppResponseDto(
			crewMember.getId(),
			crew.getId(),
			crewMemberMapping.getId(),
			new JoinCrewAppResponseDto.CrewMemberAppInfo(
				crewMember.getId(),
				crewMember.getUserId(),
				crewMemberMapping.getStatus().name()
			)
		);
	}

	public SearchCrewAppPagingResponseDto searchCrew(String keyword, Pageable pageable) {
		Page<Crew> crews = crewRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
			keyword, keyword, pageable);

		return SearchCrewAppPagingResponseDto.from(crews);
	}

	public GetSpecificCrewAppResponseDto getSpecificCrew(UUID crewId) {
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		return GetSpecificCrewAppResponseDto.from(crew);
	}

	public UpdateCrewAppResponseDto updateCrew(UUID crewId, UpdateCrewAppRequestDto dto) {
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		crew.update(dto.description(), dto.capacity(), dto.isAutoAgree());

		return new UpdateCrewAppResponseDto(
			crew.getId(),
			crew.getName(),
			crew.getDescription(),
			crew.getCaptainId(),
			crew.getCapacity(),
			crew.getIsAutoAgree()
		);
	}

	public void deleteCrew(UUID crewId) {
		Crew crew = crewRepository.findById(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		crew.setDeletedCrewMeetings();
		crew.setDeletedCrewMember();
		crew.setDeleted();
	}

	public JoinCrewAppResponseDto agreeJoiningCrew(UUID crewId) {
		return null;
	}

	public JoinCrewAppResponseDto rejectJoiningCrew(UUID crewId) {
		return null;
	}
}
