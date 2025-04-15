package com._42195km.msa.rankingservice.infrastructure.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com._42195km.msa.rankingservice.application.service.RankingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonalScheduling {

	private final RankingService rankingService;

	// 매일 새벽 2시에 실행
	@Scheduled(cron = "0 0 2 * * MON")
	public void scheduledPersonalRanking() {
		log.info("스케줄링 실행 - 개인 랭킹 생성 시작");
		rankingService.createPersonalRanking();
		log.info("스케줄링 완료 - 개인 랭킹 생성 끝");
	}
}
