package com._42195km.msa.auth.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	MASTER("MASTER"),
	COMPANY("COMPANY"),
	NORMAL("NORMAL");

	private final String value;
}
