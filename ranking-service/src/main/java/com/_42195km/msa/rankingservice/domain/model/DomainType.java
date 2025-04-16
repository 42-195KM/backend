package com._42195km.msa.rankingservice.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainType {

	CREW("CREW"),
	USER("USER");

	private final String value;
}
