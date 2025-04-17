package com._42195km.msa.crew.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._42195km.msa.common.api.ApiResponse;
import com._42195km.msa.common.resolver.UserInfo;
import com._42195km.msa.common.resolver.UserInfoDto;
import com._42195km.msa.crew.application.exception.CrewServiceCode;
import com._42195km.msa.crew.application.service.CrewService;
import com._42195km.msa.crew.presentation.dto.request.CreateCrewMeetingRequestDto;
import com._42195km.msa.crew.presentation.dto.request.CreateCrewRequestDto;
import com._42195km.msa.crew.presentation.dto.request.HandleCrewJoinRequestDto;
import com._42195km.msa.crew.presentation.dto.request.UpdateCrewMeetingRequestDto;
import com._42195km.msa.crew.presentation.dto.request.UpdateCrewRequestDto;
import com._42195km.msa.crew.presentation.dto.response.CreateCrewMeetingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.CreateCrewResponseDto;
import com._42195km.msa.crew.presentation.dto.response.ExpelCrewMemberResponseDto;
import com._42195km.msa.crew.presentation.dto.response.GetSpecificCrewMeetingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.GetSpecificCrewMemberResponseDto;
import com._42195km.msa.crew.presentation.dto.response.GetSpecificCrewResponseDto;
import com._42195km.msa.crew.presentation.dto.response.HandleCrewJoinResponseDto;
import com._42195km.msa.crew.presentation.dto.response.JoinCrewResponseDto;
import com._42195km.msa.crew.presentation.dto.response.ManageNoShowMeetingMemberResponseDto;
import com._42195km.msa.crew.presentation.dto.response.ParticipateCrewMeetingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.SearchCrewMeetingPagingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.SearchCrewMemberPagingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.SearchCrewPagingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.UpdateCrewMeetingResponseDto;
import com._42195km.msa.crew.presentation.dto.response.UpdateCrewResponseDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/crews")
@RequiredArgsConstructor
public class CrewController {
	private final CrewService crewService;

	@PostMapping
	public ResponseEntity<ApiResponse<?>> createCrew(@RequestBody CreateCrewRequestDto dto,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_CREATE_POST_SUCCESS.getCode(),
				CreateCrewResponseDto.from(crewService.createCrew(dto.toAppDto(),
					userInfoDto.userId())),
				CrewServiceCode.CREW_CREATE_POST_SUCCESS.getMessage(),
				CrewServiceCode.CREW_CREATE_POST_SUCCESS.getStatus()
			)
		);
	}

	@PostMapping("/{crewId}/join")
	public ResponseEntity<ApiResponse<?>> applyJoiningCrew(@PathVariable(name = "crewId") UUID crewId,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_APPLY_JOIN_POST_SUCCESS.getCode(),
				JoinCrewResponseDto.from(crewService.applyJoiningCrew(crewId, userInfoDto.userId())),
				CrewServiceCode.CREW_APPLY_JOIN_POST_SUCCESS.getMessage(),
				CrewServiceCode.CREW_APPLY_JOIN_POST_SUCCESS.getStatus()
			)
		);

	}

	@GetMapping("/{crewId}")
	public ResponseEntity<ApiResponse<?>> getSpecificCrew(@PathVariable(name = "crewId") UUID crewId) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_SPECIFIC_GET_SUCCESS.getCode(),
				GetSpecificCrewResponseDto.from(crewService.getSpecificCrew(crewId)),
				CrewServiceCode.CREW_SPECIFIC_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_SPECIFIC_GET_SUCCESS.getStatus()
			)
		);
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<?>> searchCrew(
		@RequestParam(name = "keyword", required = false) String keyword,
		@PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_SEARCH_GET_SUCCESS.getCode(),
				SearchCrewPagingResponseDto.from(crewService.searchCrew(keyword, pageable)),
				CrewServiceCode.CREW_SEARCH_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_SEARCH_GET_SUCCESS.getStatus()
			)
		);
	}

	@PatchMapping("/{crewId}")
	public ResponseEntity<ApiResponse<?>> updateCrew(@PathVariable(name = "crewId") UUID crewId,
		@UserInfo UserInfoDto userInfoDto,
		@RequestBody UpdateCrewRequestDto dto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_UPDATE_PATCH_SUCCESS.getCode(),
				UpdateCrewResponseDto.from(crewService.updateCrew(crewId, userInfoDto.userId(), dto.toAppDto())),
				CrewServiceCode.CREW_UPDATE_PATCH_SUCCESS.getMessage(),
				CrewServiceCode.CREW_UPDATE_PATCH_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}")
	public ResponseEntity<ApiResponse<?>> deleteCrew(@PathVariable(name = "crewId") UUID crewId,
		@UserInfo UserInfoDto userInfoDto) {
		crewService.deleteCrew(crewId, userInfoDto.userId());
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_DELETE_DELETE_SUCCESS.getCode(),
				null,
				CrewServiceCode.CREW_DELETE_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_DELETE_DELETE_SUCCESS.getStatus()
			)
		);
	}

	@PatchMapping("/{crewId}/agree")
	public ResponseEntity<ApiResponse<?>> agreeJoiningCrew(
		@RequestBody HandleCrewJoinRequestDto dto,
		@PathVariable(name = "crewId") UUID crewId,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_AGREE_JOIN_PATCH_SUCCESS.getCode(),
				HandleCrewJoinResponseDto.from(crewService.agreeJoiningCrew(
					dto.toAppDto(),
					crewId,
					userInfoDto.userId())),
				CrewServiceCode.CREW_AGREE_JOIN_PATCH_SUCCESS.getMessage(),
				CrewServiceCode.CREW_AGREE_JOIN_PATCH_SUCCESS.getStatus()
			)
		);
	}

	@PatchMapping("/{crewId}/reject")
	public ResponseEntity<ApiResponse<?>> rejectJoiningCrew(
		@RequestBody HandleCrewJoinRequestDto dto,
		@PathVariable(name = "crewId") UUID crewId,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_REJECT_JOIN_PATCH_SUCCESS.getCode(),
				HandleCrewJoinResponseDto.from(crewService.rejectJoiningCrew(
					dto.toAppDto(),
					crewId,
					userInfoDto.userId())),
				CrewServiceCode.CREW_REJECT_JOIN_PATCH_SUCCESS.getMessage(),
				CrewServiceCode.CREW_REJECT_JOIN_PATCH_SUCCESS.getStatus()
			)
		);
	}

	@GetMapping("/{crewId}/members/search")
	public ResponseEntity<ApiResponse<?>> searchMembers(
		@PathVariable(name = "crewId") UUID crewId,
		@PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		//  TODO : 크루원 프로필이나 닉네임을 추가

		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_MEMBER_SEARCH_GET_SUCCESS.getCode(),
				SearchCrewMemberPagingResponseDto.from(crewService.searchCrewMember(crewId, pageable)),
				CrewServiceCode.CREW_MEMBER_SEARCH_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_MEMBER_SEARCH_GET_SUCCESS.getStatus()
			)
		);
	}

	@GetMapping("/{crewId}/members/{memberId}")
	public ResponseEntity<ApiResponse<?>> getMember(@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "memberId") UUID memberId) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_MEMBER_SPECIFIC_GET_SUCCESS.getCode(),
				GetSpecificCrewMemberResponseDto.from(crewService.getSpecificCrewMember(crewId, memberId)),
				CrewServiceCode.CREW_MEMBER_SPECIFIC_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_MEMBER_SPECIFIC_GET_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}/members/{memberId}")
	public ResponseEntity<ApiResponse<?>> expelMember(@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "memberId") UUID memberId,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_EXPEL_DELETE_SUCCESS.getCode(),
				ExpelCrewMemberResponseDto.from(crewService.expel(crewId, memberId, userInfoDto.userId())),
				CrewServiceCode.CREW_EXPEL_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_EXPEL_DELETE_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}/members/{memberId}/leave")
	public ResponseEntity<ApiResponse<?>> leaveMember(@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "memberId") UUID memberId,
		@UserInfo UserInfoDto userInfoDto) {
		crewService.leaveCrew(crewId, memberId, userInfoDto.userId());
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_LEAVE_DELETE_SUCCESS.getCode(),
				null,
				CrewServiceCode.CREW_LEAVE_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_LEAVE_DELETE_SUCCESS.getStatus()
			)
		);
	}

	@PostMapping("/{crewId}/meetings")
	public ResponseEntity<ApiResponse<?>> createCrewMeeting(@RequestBody CreateCrewMeetingRequestDto dto,
		@PathVariable(name = "crewId") UUID crewId,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_CREATE_MEETING_POST_SUCCESS.getCode(),
				CreateCrewMeetingResponseDto.from(
					crewService.createCrewMeeting(dto.toAppDto(), crewId, userInfoDto.userId())),
				CrewServiceCode.CREW_CREATE_MEETING_POST_SUCCESS.getMessage(),
				CrewServiceCode.CREW_CREATE_MEETING_POST_SUCCESS.getStatus()
			)
		);
	}

	@PostMapping("/{crewId}/meetings/{meetingId}/participate")
	public ResponseEntity<ApiResponse<?>> participateCrewMeeting(
		@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "meetingId") UUID meetingId,
		@UserInfo UserInfoDto userInfoDto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_PARTICIPATE_MEETING_POST_SUCCESS.getCode(),
				ParticipateCrewMeetingResponseDto.from(crewService.participateCrewMeeting(
					crewId,
					meetingId,
					userInfoDto.userId()
				)),
				CrewServiceCode.CREW_PARTICIPATE_MEETING_POST_SUCCESS.getMessage(),
				CrewServiceCode.CREW_PARTICIPATE_MEETING_POST_SUCCESS.getStatus()
			)
		);
	}

	@PatchMapping("/{crewId}/meetings/{meetingId}")
	public ResponseEntity<ApiResponse<?>> updateCrewMeeting(
		@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "meetingId") UUID meetingId,
		@UserInfo UserInfoDto userInfoDto,
		@RequestBody UpdateCrewMeetingRequestDto dto) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_UPDATE_MEETING_SUCCESS.getCode(),
				UpdateCrewMeetingResponseDto.from(crewService.updateCrewMeeting(
					dto.toAppDto(), crewId, meetingId, userInfoDto.userId()
				)),
				CrewServiceCode.CREW_UPDATE_MEETING_SUCCESS.getMessage(),
				CrewServiceCode.CREW_UPDATE_MEETING_SUCCESS.getStatus()
			)
		);
	}

	@GetMapping("/{crewId}/meetings/{meetingId}")
	public ResponseEntity<ApiResponse<?>> getSpecificCrewMeeting(
		@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "meetingId") UUID meetingId) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_MEETING_SPECIFIC_GET_SUCCESS.getCode(),
				GetSpecificCrewMeetingResponseDto.from(crewService.getSpecificCrewMeeting(crewId, meetingId)),
				CrewServiceCode.CREW_MEETING_SPECIFIC_GET_SUCCESS.getMessage(),
				CrewServiceCode.CREW_MEETING_SPECIFIC_GET_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}/meetings/{meetingId}/noShow/{meetingMemberId}")
	public ResponseEntity<ApiResponse<?>> manageNoShowMeetingMember(
		@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "meetingId") UUID meetingId,
		@PathVariable(name = "meetingMemberId") UUID meetingMemberId,
		@UserInfo UserInfoDto userInfoDto
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_MANAGE_NO_SHOW_DELETE_SUCCESS.getCode(),
				ManageNoShowMeetingMemberResponseDto.from(crewService.manageNoShowMeetingMember(
					crewId,
					meetingId,
					meetingMemberId,
					userInfoDto.userId()
				)),
				CrewServiceCode.CREW_MANAGE_NO_SHOW_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_MANAGE_NO_SHOW_DELETE_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}/meetings/{meetingId}/{meetingMemberId}")
	public ResponseEntity<ApiResponse<?>> leaveMeeting(
		@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "meetingId") UUID meetingId,
		@PathVariable(name = "meetingMemberId") UUID meetingMemberId,
		@UserInfo UserInfoDto userInfoDto
	) {
		crewService.leaveMeeting(crewId, meetingId, meetingMemberId, userInfoDto.userId());
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_LEAVE_MEETING_DELETE_SUCCESS.getCode(),
				null,
				CrewServiceCode.CREW_LEAVE_MEETING_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_LEAVE_MEETING_DELETE_SUCCESS.getStatus()
			)
		);
	}

	@GetMapping("/{crewId}/meetings/search")
	public ResponseEntity<ApiResponse<?>> searchCrewMeeting(
		@PathVariable(name = "crewId") UUID crewId,
		@PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_SEARCH_MEETING_SEARCH_SUCCESS.getCode(),
				SearchCrewMeetingPagingResponseDto.from(crewService.searchCrewMeeting(crewId, pageable)),
				CrewServiceCode.CREW_SEARCH_MEETING_SEARCH_SUCCESS.getMessage(),
				CrewServiceCode.CREW_SEARCH_MEETING_SEARCH_SUCCESS.getStatus()
			)
		);
	}

	@DeleteMapping("/{crewId}/meetings/{meetingId}")
	public ResponseEntity<ApiResponse<?>> deleteCrewMeeting(
		@PathVariable(name = "crewId") UUID crewId,
		@PathVariable(name = "meetingId") UUID meetingId,
		@UserInfo UserInfoDto userInfoDto) {
		crewService.deleteCrewMeeting(crewId, meetingId, userInfoDto.userId());
		return ResponseEntity.ok(
			new ApiResponse<>(
				CrewServiceCode.CREW_DELETE_MEETING_DELETE_SUCCESS.getCode(),
				null,
				CrewServiceCode.CREW_DELETE_MEETING_DELETE_SUCCESS.getMessage(),
				CrewServiceCode.CREW_DELETE_MEETING_DELETE_SUCCESS.getStatus()
			)
		);
	}

}

