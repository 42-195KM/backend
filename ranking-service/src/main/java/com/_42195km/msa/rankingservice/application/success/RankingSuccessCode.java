package com._42195km.msa.rankingservice.application.success;

import org.springframework.http.HttpStatus;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RankingSuccessCode implements ServiceCode {

	PERSONAL_RANKING_SCHEDULING(HttpStatus.CREATED, "개인 랭킹 스케쥴링 완료", "S_PERSONAL_RANKING_SCHEDULING"),
	PERSONAL_RANKING_ALL_SEARCH_SUCCESS(HttpStatus.OK, "모든 랭킹 조회가 성공적으로 완료되었습니다.",
		"S_PERSONAL_RANKING_ALL_SEARCH_SUCCESS"),
	PERSONAL_RANKING_SEARCH_SUCCESS(HttpStatus.OK, "단건 랭킹 조회가 성공적으로 완료되었습니다.", "S_PERSONAL_RANKING_SEARCH_SUCCESS"),
	KEYWORD_PERSONAL_RANKING_SEARCH_SUCCESS(HttpStatus.OK, "키워드 조회가 성공적으로 완료되었습니다.",
		"S_KEYWORD_PERSONAL_RANKING_SEARCH_SUCCESS"),
	PERSONAL_RANKING_DELETE_SUCCESS(HttpStatus.NO_CONTENT, "랭킹 삭제가 성공적으로 완료되었습니다.",
		"S_PERSONAL_RANKING_DELETE_SUCCESS"),
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
