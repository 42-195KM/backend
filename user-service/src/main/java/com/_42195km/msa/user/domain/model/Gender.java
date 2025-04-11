package com._42195km.msa.user.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
	M("M"),
	F("F");

	private final String value;
}
