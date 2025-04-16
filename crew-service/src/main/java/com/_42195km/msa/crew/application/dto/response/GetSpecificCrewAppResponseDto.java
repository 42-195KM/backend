package com._42195km.msa.crew.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.crew.domain.model.Crew;

public record GetSpecificCrewAppResponseDto(
	UUID id,
	String name,
	String description,
	UUID captainId,
	Integer capacity,
	Boolean isAutoAgree,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	List<CrewMemberAppMappingInfo> crewMembers
) {

	public static GetSpecificCrewAppResponseDto from(Crew crew) {
		return new GetSpecificCrewAppResponseDto(
			crew.getId(),
			crew.getName(),
			crew.getDescription(),
			crew.getCaptainId(),
			crew.getCapacity(),
			crew.getIsAutoAgree(),
			crew.getCreatedAt(),
			crew.getUpdatedAt(),
			crew.getCrewMemberMappings().stream().map(
				crewMemberMapping -> new CrewMemberAppMappingInfo(
					crewMemberMapping.getId(),
					new CrewMemberAppInfo(
						crewMemberMapping.getCrewMember().getId(),
						crewMemberMapping.getCrewMember().getUserId(),
						crewMemberMapping.getStatus().name()
					)
				)
			).toList()
		);
	}

	public record CrewMemberAppMappingInfo(UUID id, CrewMemberAppInfo crewMember) {
	}

	public record CrewMemberAppInfo(UUID id, UUID userId, String status) {
	}

}
