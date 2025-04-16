package com._42195km.msa.achievementservice.infrastructure.messaging.in;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserEventDto {
	private UUID userId;
}
