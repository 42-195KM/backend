package com_42195km.msa.achievementservice.presentation.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com_42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com_42195km.msa.achievementservice.application.service.AchivementService;
import com_42195km.msa.achievementservice.domain.repository.AchievementRepository;
import com_42195km.msa.achievementservice.presentation.dto.request.CreateAchievementRequestDto;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AchivementController {

	private final AchivementService achievementService;

	// 업적 생성 (POST api/v1/app/achivements)
	@PostMapping("/app/achivements")
	public ResponseEntity<?> createAchievement(@RequestBody CreateAchievementRequestDto createAchievementCommandDto) {
		return null;
	}

	// 업적 조회 (GET api/vi/achivements/{achivementId})
	@GetMapping("/achivements/{achievementId}")
	public ResponseEntity<?> getAchievement(@PathVariable UUID achievementId) {
		return null;
	}

	// 업적 목록 보기 (GET api/v1/achivements)
	@GetMapping("/achivements")
	public ResponseEntity<?> getAllAchievements() {
		return null;
	}

	// 업적 검색 (GET api/v1/achivements/search?title={title})
	@GetMapping("/achivements/search")
	public ResponseEntity<?> searchAchivements(
		@RequestParam("title") String title
	){
		return null;
	}

	// 특정 사용자가 달성한 업적 검색 (GET api/v1/achivements/user/{userId})
	@GetMapping("/achivements/user/{userId}")
	public ResponseEntity<?> getAchievementsByUserId(
		@PathVariable("userId") UUID userId
	){
		return null;
	}

	// 업적 삭제 (DELETE api/vi/achivements/{achivementId})
	@DeleteMapping("/achivements/{achievementId}")
	public ResponseEntity<?> deleteAchievement(@PathVariable UUID achievementId){
		return null;
	}
}
