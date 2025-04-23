package com._42195km.msa.auth.infrastructure.messaging.in;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DeleteUserEventDto {

	private UUID userId;
}
