package com._42195km.msa.crew.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com._42195km.msa.crew.application.dto.response.GetSpecificCrewAppResponseDto;

public record GetSpecificCrewResponseDto(
	UUID id,
	String name,
	String description,
	UUID captainId,
	Integer capacity,
	Boolean isAutoAgree,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	List<CrewMemberMappingInfo> crewMembers
) {

	public record CrewMemberMappingInfo(UUID id, CrewMemberInfo crewMember) {
	}

	public record CrewMemberInfo(UUID id, UUID userId, String status) {
	}

	public static GetSpecificCrewResponseDto from(GetSpecificCrewAppResponseDto dto) {
		return new GetSpecificCrewResponseDto(
			dto.id(),
			dto.name(),
			dto.description(),
			dto.captainId(),
			dto.capacity(),
			dto.isAutoAgree(),
			dto.createdAt(),
			dto.updatedAt(),
			dto.crewMembers().stream().map(
				crewMemberMapping -> new CrewMemberMappingInfo(
					crewMemberMapping.id(),
					new CrewMemberInfo(
						crewMemberMapping.crewMember().id(),
						crewMemberMapping.crewMember().userId(),
						crewMemberMapping.crewMember().status()
					)
				)
			).toList()
		);
	}

}
