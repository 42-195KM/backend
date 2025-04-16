package com._42195km.msa.crew.application.dto.response;

import java.util.UUID;

public record CreateCrewAppResponseDto(
	UUID id,
	String name,
	String description,
	UUID captainId,
	Integer capacity,
	Boolean isAutoAgree
) {
}
