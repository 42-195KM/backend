package com._42195km.msa.user.infrastructure.messaging.out;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserEventType {
	CREATE("CREATE"),
	UPDATE("UPDATE"),
	DELETE("DELETE");

	private final String value;
}
