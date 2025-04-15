package com._42195km.msa.rankingservice.application.exception;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RankingException implements ServiceCode {

	NOT_FOUND_PERSONAL_RANKING(HttpStatus.NOT_FOUND, "개인 랭킹을 찾지 못했습니다.", "E_NOT_FOUND_PERSONAL_RANKING"),
	RANKING_EMPTY(HttpStatus.NOT_FOUND, "랭킹 리스트가 비었습니다.", "E_RANKING_EMPTY"),
	;

	private final HttpStatus httpStatus;
	private final String message;
	private final String errorCode;

	@Override
	public String getCode() {
		return this.errorCode;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public int getStatus() {
		return this.httpStatus.value();
	}
}
