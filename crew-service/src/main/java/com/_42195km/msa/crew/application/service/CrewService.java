package com._42195km.msa.crew.application.service;

import static com._42195km.msa.crew.domain.model.CrewMemberMapping.CrewMemberStatus.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.code.CommonServiceCode;
import com._42195km.msa.crew.application.dto.request.CreateCrewAppRequestDto;
import com._42195km.msa.crew.application.dto.request.CreateCrewMeetingAppRequestDto;
import com._42195km.msa.crew.application.dto.request.HandleCrewJoinAppRequestDto;
import com._42195km.msa.crew.application.dto.request.UpdateCrewAppRequestDto;
import com._42195km.msa.crew.application.dto.request.UpdateCrewMeetingAppRequestDto;
import com._42195km.msa.crew.application.dto.response.CreateCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.CreateCrewMeetingAppResponseDto;
import com._42195km.msa.crew.application.dto.response.ExpelCrewMemberAppResponseDto;
import com._42195km.msa.crew.application.dto.response.GetSpecificCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.GetSpecificCrewMeetingAppResponseDto;
import com._42195km.msa.crew.application.dto.response.GetSpecificCrewMemberAppResponseDto;
import com._42195km.msa.crew.application.dto.response.HandleCrewJoinAppResponseDto;
import com._42195km.msa.crew.application.dto.response.JoinCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.ManageNoShowMeetingMemberAppResponseDto;
import com._42195km.msa.crew.application.dto.response.ParticipateCrewMeetingAppResponseDto;
import com._42195km.msa.crew.application.dto.response.SearchCrewAppPagingResponseDto;
import com._42195km.msa.crew.application.dto.response.SearchCrewMeetingAppPagingResponseDto;
import com._42195km.msa.crew.application.dto.response.SearchCrewMemberAppPagingResponseDto;
import com._42195km.msa.crew.application.dto.response.UpdateCrewAppResponseDto;
import com._42195km.msa.crew.application.dto.response.UpdateCrewMeetingAppResponseDto;
import com._42195km.msa.crew.application.exception.CrewBusinessException;
import com._42195km.msa.crew.application.exception.CrewServiceCode;
import com._42195km.msa.crew.domain.model.Crew;
import com._42195km.msa.crew.domain.model.CrewMeeting;
import com._42195km.msa.crew.domain.model.CrewMeetingMember;
import com._42195km.msa.crew.domain.model.CrewMeetingMemberMapping;
import com._42195km.msa.crew.domain.model.CrewMember;
import com._42195km.msa.crew.domain.model.CrewMemberMapping;
import com._42195km.msa.crew.domain.repository.CrewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrewService {
	private final CrewRepository crewRepository;

	@Transactional
	public CreateCrewAppResponseDto createCrew(CreateCrewAppRequestDto dto, UUID userId) {
		if (crewRepository.existsByName(dto.name())) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_NAME_DUPLICATED);
		}

		Crew crew = Crew.builder()
			.name(dto.name())
			.description(dto.description())
			.capacity(dto.capacity())
			.captainId(userId)
			.isAutoAgree(dto.isAutoAgree())
			.build();

		crewRepository.save(crew);

		return new CreateCrewAppResponseDto(
			crew.getId(),
			crew.getName(),
			crew.getDescription(),
			crew.getCaptainId(),
			crew.getCapacity(),
			crew.getIsAutoAgree()
		);
	}

	@Transactional
	public JoinCrewAppResponseDto applyJoiningCrew(UUID crewId, UUID userId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isFull()) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_IS_FULL);
		}

		if (crew.isAlreadyJoined(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_MEMBER_ALREADY_JOINED);
		}

		if (crew.isInBlackList(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_MEMBER_IN_BLACK_LIST);
		}

		CrewMember crewMember = CrewMember.builder()
			.userId(userId)
			.build();

		CrewMemberMapping crewMemberMapping = CrewMemberMapping.builder()
			.crew(crew)
			.crewMember(crewMember)
			.status(crew.getIsAutoAgree() ? APPROVED : PENDING)
			.build();

		crew.addCrewMemberMapping(crewMemberMapping);
		crewMember.addCrewMemberMapping(crewMemberMapping);
		crewRepository.save(crew);

		return new JoinCrewAppResponseDto(
			crewMember.getId(),
			crew.getId(),
			crewMemberMapping.getId(),
			new JoinCrewAppResponseDto.CrewMemberAppInfo(
				crewMember.getId(),
				crewMember.getUserId(),
				crewMemberMapping.getStatus().name()
			)
		);
	}

	@Transactional
	public SearchCrewAppPagingResponseDto searchCrew(String keyword, Pageable pageable) {
		if (keyword == null) {
			Page<Crew> crews = crewRepository.findAllByDeletedAtIsNull(pageable);
			return SearchCrewAppPagingResponseDto.from(crews);
		}

		Page<Crew> crews = crewRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndDeletedAtIsNull(
			keyword, keyword, pageable);

		return SearchCrewAppPagingResponseDto.from(crews);
	}

	@Transactional
	public GetSpecificCrewAppResponseDto getSpecificCrew(UUID crewId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		return GetSpecificCrewAppResponseDto.from(crew);
	}

	@Transactional
	public UpdateCrewAppResponseDto updateCrew(UUID crewId, UUID userId, UpdateCrewAppRequestDto dto) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isNotCaptain(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
		}

		crew.update(dto.description(), dto.capacity(), dto.isAutoAgree());

		return new UpdateCrewAppResponseDto(
			crew.getId(),
			crew.getName(),
			crew.getDescription(),
			crew.getCaptainId(),
			crew.getCapacity(),
			crew.getIsAutoAgree()
		);
	}

	@Transactional
	public void deleteCrew(UUID crewId, UUID userId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isNotCaptain(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
		}

		crew.setDeletedCrewMeetings();
		crew.setDeletedCrewMember();
		crew.setDeletedCrewMemberMappings();
		crew.setDeleted();
	}

	@Transactional
	public HandleCrewJoinAppResponseDto agreeJoiningCrew(HandleCrewJoinAppRequestDto dto, UUID crewId, UUID captainId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isNotCaptain(captainId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
		}

		CrewMemberMapping result = crew.approve(dto.userId());

		return HandleCrewJoinAppResponseDto.from(result);
	}

	@Transactional
	public HandleCrewJoinAppResponseDto rejectJoiningCrew(HandleCrewJoinAppRequestDto dto, UUID crewId,
		UUID captainId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isNotCaptain(captainId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
		}

		CrewMemberMapping result = crew.reject(dto.userId());

		return HandleCrewJoinAppResponseDto.from(result);
	}

	@Transactional
	public GetSpecificCrewMemberAppResponseDto getSpecificCrewMember(UUID crewId, UUID memberId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		return GetSpecificCrewMemberAppResponseDto.from(
			crew.findCrewMemberMappingByUserId(memberId)
		);
	}

	@Transactional
	public SearchCrewMemberAppPagingResponseDto searchCrewMember(UUID crewId, Pageable pageable) {
		return SearchCrewMemberAppPagingResponseDto.from(
			crewRepository.findAllCrewMemberMappingByCrewId(crewId, pageable)
		);
	}

	@Transactional
	public ExpelCrewMemberAppResponseDto expel(UUID crewId, UUID memberId, UUID captainId) {
		// 크루장이 강퇴하는 지 검증
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isNotCaptain(captainId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
		}

		return ExpelCrewMemberAppResponseDto.from(crew.expel(memberId));

	}

	@Transactional
	public void leaveCrew(UUID crewId, UUID memberId, UUID userId) {
		// 유저 본인 요청인지 검증
		if (!memberId.equals(userId)) {
			throw CrewBusinessException.from(CommonServiceCode.FORBIDDEN);
		}

		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		crew.removeCrewMemberMapping(memberId);
	}

	@Transactional
	public CreateCrewMeetingAppResponseDto createCrewMeeting(CreateCrewMeetingAppRequestDto dto, UUID crewId,
		UUID userId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (CrewMeeting.isRegularMeetingRequest(dto.type())) {
			if (crew.isNotCaptain(userId)) {
				throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
			}
		}

		CrewMeeting crewMeeting = CrewMeeting.builder()
			.name(dto.name())
			.meetingDateTime(dto.date())
			.hour(dto.hour())
			.description(dto.description())
			.type(dto.type())
			.capacity(dto.capacity())
			.build();

		crew.addCrewMeeting(crewMeeting);
		crewRepository.save(crew);

		return new CreateCrewMeetingAppResponseDto(
			crewMeeting.getId(),
			crew.getId(),
			crewMeeting.getName(),
			crewMeeting.getMeetingDateTime(),
			crewMeeting.getHour(),
			crewMeeting.getDescription(),
			crewMeeting.getType().name(),
			crewMeeting.getCapacity()
		);
	}

	@Transactional
	public ParticipateCrewMeetingAppResponseDto participateCrewMeeting(UUID crewId, UUID meetingId, UUID userId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		CrewMeeting crewMeeting = crew.findCrewMeeting(meetingId);

		if (crew.isNotMember(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_MEMBER_NOT_FOUND);
		}

		if (crewMeeting.isAlreadyParticipated(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.CREW_MEETING_ALREADY_PARTICIPATED);
		}

		if (crewMeeting.isRegularMeeting()) {
			if (crewMeeting.isFull()) {
				throw CrewBusinessException.from(CrewServiceCode.CREW_REGULAR_MEETING_IS_FULL);
			}
		}

		CrewMeetingMember crewMeetingMember = CrewMeetingMember.builder()
			.userId(userId)
			.build();

		CrewMeetingMemberMapping crewMeetingMemberMapping = CrewMeetingMemberMapping.builder()
			.meeting(crewMeeting)
			.meetingMember(crewMeetingMember)
			.status(CrewMeetingMemberMapping.MeetingMemberStatus.APPROVED)
			.build();

		crew.addCrewMeeting(crewMeeting);
		crewMeeting.addCrewMeetingMemberMapping(crewMeetingMemberMapping);
		crewMeetingMember.addCrewMeetingMemberMapping(crewMeetingMemberMapping);
		crewRepository.save(crew);

		return new ParticipateCrewMeetingAppResponseDto(
			crew.getId(),
			crewMeetingMemberMapping.getId(),
			new ParticipateCrewMeetingAppResponseDto.CrewMeetingMemberAppInfo(
				crewMeetingMember.getId(),
				crewMeetingMember.getUserId(),
				crewMeetingMemberMapping.getStatus().name()
			)
		);
	}

	@Transactional
	public UpdateCrewMeetingAppResponseDto updateCrewMeeting(
		UpdateCrewMeetingAppRequestDto dto, UUID crewId, UUID meetingId, UUID userId
	) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		CrewMeeting crewMeeting = crew.findCrewMeeting(meetingId);

		if (crewMeeting.getCreatedBy() != userId) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_MEETING_ACCESS);
		}

		crewMeeting.update(
			dto.name(),
			dto.hour(),
			dto.description(),
			dto.capacity()
		);

		crewRepository.save(crew);

		return new UpdateCrewMeetingAppResponseDto(
			crewMeeting.getId(),
			crew.getId(),
			crewMeeting.getName(),
			crewMeeting.getMeetingDateTime(),
			crewMeeting.getHour(),
			crewMeeting.getDescription(),
			crewMeeting.getType().name(),
			crewMeeting.getCapacity()
		);
	}

	public GetSpecificCrewMeetingAppResponseDto getSpecificCrewMeeting(UUID crewId, UUID meetingId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		CrewMeeting crewMeeting = crew.findCrewMeeting(meetingId);
		List<CrewMeetingMemberMapping> crewMeetingMemberMappings = crewMeeting.getCrewMeetingMemberMappings();

		return new GetSpecificCrewMeetingAppResponseDto(
			crewMeeting.getId(),
			crew.getId(),
			crewMeeting.getName(),
			crewMeeting.getMeetingDateTime(),
			crewMeeting.getHour(),
			crewMeeting.getDescription(),
			crewMeeting.getType().name(),
			crewMeeting.getCapacity(),
			crewMeetingMemberMappings.stream()
				.map(mapping -> new GetSpecificCrewMeetingAppResponseDto.MeetingMemberAppInfo(
					mapping.getId(),
					mapping.getMeetingMember().getUserId(),
					mapping.getStatus().name()

				))
				.toList()
		);
	}

	@Transactional(readOnly = true)
	public SearchCrewMeetingAppPagingResponseDto searchCrewMeeting(UUID crewId, Pageable pageable) {
		return SearchCrewMeetingAppPagingResponseDto.from(
			crewRepository.findAllCrewMeetingByCrewId(crewId, pageable)
		);
	}

	public ManageNoShowMeetingMemberAppResponseDto manageNoShowMeetingMember(UUID crewId, UUID meetingId,
		UUID meetingMemberId, UUID captainId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		if (crew.isNotCaptain(captainId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_ACCESS);
		}

		CrewMeeting crewMeeting = crew.findCrewMeeting(meetingId);
		CrewMeetingMemberMapping crewMeetingMemberMapping = crewMeeting.findCrewMeetingMemberMapping(meetingMemberId);
		crewMeetingMemberMapping.manageNoShow();
		crewRepository.save(crew);

		return new ManageNoShowMeetingMemberAppResponseDto(
			crew.getId(),
			crewMeetingMemberMapping.getMeeting().getId(),
			new ManageNoShowMeetingMemberAppResponseDto.MeetingMemberAppInfo(
				crewMeetingMemberMapping.getId(),
				crewMeetingMemberMapping.getMeetingMember().getUserId(),
				crewMeetingMemberMapping.getStatus().name()
			)
		);
	}

	@Transactional
	public void leaveMeeting(UUID crewId, UUID meetingId, UUID meetingMemberId, UUID userId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		CrewMeeting crewMeeting = crew.findCrewMeeting(meetingId);

		if (!meetingMemberId.equals(userId)) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_MEETING_DELETE_ACCESS);
		}

		crewMeeting.removeMeetingMember(meetingMemberId);
	}
	// TODO : 애그리거트를 이용해 상태변경하도록 수정
	// TODO : 모임 시간이 끝나면 모임이 자동 출석되도록 하는 이벤트 추가
	// TODO : 메서드 순서 정리

	@Transactional
	public void deleteCrewMeeting(UUID crewId, UUID meetingId, UUID meetingCaptainId) {
		Crew crew = crewRepository.findByIdAndDeletedAtIsNull(crewId)
			.orElseThrow(() -> CrewBusinessException.from(CrewServiceCode.CREW_NOT_FOUND));

		CrewMeeting crewMeeting = crew.findCrewMeeting(meetingId);

		if (crewMeeting.getCreatedBy() != meetingCaptainId) {
			throw CrewBusinessException.from(CrewServiceCode.UNAUTHORIZED_CREW_MEETING_ACCESS);
		}

		crewMeeting.deleteCrewMemberMappings();
		crewMeeting.setDeleted();
		crewRepository.save(crew);
	}
}
