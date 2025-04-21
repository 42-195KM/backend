package com._42195km.msa.achievementservice.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.aop.CheckPermission;
import com._42195km.msa.common.api.ApiResponse;

import com._42195km.msa.achievementservice.application.dto.request.CreateAchievementCommandDto;
import com._42195km.msa.achievementservice.application.service.AchivementService;
import com._42195km.msa.achievementservice.domain.model.Achievement;
import com._42195km.msa.achievementservice.infrastructure.code.AchievementServiceCode;
import com._42195km.msa.achievementservice.presentation.dto.request.CreateAchievementRequestDto;
import com._42195km.msa.achievementservice.presentation.dto.response.CreateAchievementResponseDto;
import com._42195km.msa.achievementservice.presentation.dto.response.DeleteAchievementResponseDto;
import com._42195km.msa.achievementservice.presentation.dto.response.GetAchievementResponseDto;
import com._42195km.msa.common.controller.BaseController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AchievementController extends BaseController {

	private final AchivementService achievementService;

	// 업적 생성 (POST api/v1/app/achivements)
	@PostMapping("/app/achivements")
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<?> createAchievement(@RequestBody CreateAchievementRequestDto dto) {
		CreateAchievementCommandDto createAchievementCommandDto = dto.toCommandDto();
		Achievement achievement = achievementService.createAchievement(createAchievementCommandDto);
		CreateAchievementResponseDto responseDto = new CreateAchievementResponseDto(achievement);

		return createOkResponseEntity(responseDto, AchievementServiceCode.ACHIEVEMENT_CREATE_SUCCESS);
	}

	// 업적 조회 (GET api/vi/achivements/{achivementId})
	@GetMapping("/achivements/{achievementId}")
	public ResponseEntity<?> getAchievement(@PathVariable UUID achievementId) {
		Achievement achievement = achievementService.getAchievementById(achievementId);
		GetAchievementResponseDto responseDto = new GetAchievementResponseDto(achievement);

		return createOkResponseEntity(responseDto, AchievementServiceCode.ACHIEVEMENT_GET_SUCCESS);
	}

	// 업적 목록 보기 (GET api/v1/achivements)
	@GetMapping("/achivements")
	public ResponseEntity<?> getAllAchievements(
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size)
	{
		Pageable pageable = PageRequest.of(page, size);
		Page<GetAchievementResponseDto> responseDtos = achievementService.getAchievements(pageable)
			.map(GetAchievementResponseDto::new);

		return createOkResponseEntity(responseDtos, AchievementServiceCode.ACHIEVEMENT_GET_ALL_SUCCESS);
	}

	// 업적 검색 (GET api/v1/achivements/search?title={title})
	@GetMapping("/achivements/search")
	public ResponseEntity<?> searchAchivements(
		@RequestParam("title") String title,
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size)
	{
		Pageable pageable = PageRequest.of(page, size);
		Page<GetAchievementResponseDto> responseDtos = achievementService.searchAchievements(title, pageable)
			.map(GetAchievementResponseDto::new);

		return createOkResponseEntity(responseDtos, AchievementServiceCode.ACHIEVEMENT_SEARCH_SUCCESS);
	}

	// 특정 사용자가 달성한 업적 검색 (GET api/v1/achivements/user/{userId})
	@GetMapping("/achivements/user/{userId}")
	public ResponseEntity<?> getAchievementsByUserId(
		@PathVariable("userId") UUID userId,
		@RequestParam(defaultValue = "0", required = false) int page,
		@RequestParam(defaultValue = "10", required = false) int size
	){
		Pageable pageable = PageRequest.of(page, size);
		Page<GetAchievementResponseDto> responseDtos = achievementService.getAchivementsByUser(userId, pageable)
			.map(GetAchievementResponseDto::new);

		return createOkResponseEntity(responseDtos, AchievementServiceCode.ACHIEVEMENT_GET_BY_USER_SUCCESS);
	}

	// 업적 삭제 (DELETE api/vi/achivements/{achivementId})
	@DeleteMapping("/achivements/{achievementId}")
	@CheckPermission(roles = {"MASTER"}, mode = CheckPermission.Mode.ALL)
	public ResponseEntity<?> deleteAchievement(@PathVariable UUID achievementId){
		Achievement achievement = achievementService.deleteAchievement(achievementId);
		DeleteAchievementResponseDto responseDto = new DeleteAchievementResponseDto(achievement);

		return createOkResponseEntity(responseDto, AchievementServiceCode.ACHIEVEMENT_DELETE_SUCCESS);
	}
}
