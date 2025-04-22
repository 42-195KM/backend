package com._42195km.msa.competitionservice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.response.ParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.dto.response.SearchParticipantAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.ParticipantMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionParticipantMapping;
import com._42195km.msa.competitionservice.domain.model.Participant;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionParticipantMappingRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;
import com._42195km.msa.competitionservice.presentation.dto.request.CancelParticipantRequestDto;

@ExtendWith(MockitoExtension.class)
@EnableAutoConfiguration(exclude = {
	KafkaAutoConfiguration.class,
	RedisAutoConfiguration.class,
	EurekaClientAutoConfiguration.class
})
class ParticipantServiceTest {


	@InjectMocks
	private ParticipantService participantService;

	@Mock
	private ParticipantRepositoryImpl participantRepository;

	@Mock
	private CompetitionRepositoryImpl competitionRepository;

	@Mock
	private CompetitionParticipantMappingRepositoryImpl mappingRepository;

	@Mock
	private ParticipantMapper participantMapper;

	private UUID competitionId;
	private UUID participantUserId; // Participant 엔티티가 갖는 participantId(사용자 UUID)
	private Competition mockCompetition;
	private Participant mockParticipant;
	private CompetitionParticipantMapping mockMapping;

	@BeforeEach
	void setUp() {
		competitionId = UUID.randomUUID();
		participantUserId = UUID.randomUUID();

		// 대회 엔티티
		mockCompetition = Competition.builder()
			.userId(UUID.randomUUID())
			.title("테스트 대회")
			.type(null)     // 필요 시 CompetitionType 설정
			.receptionType(null) // 필요 시 ReceptionType 설정
			.participantsNum(10)
			.price(10000)
			.build();

		// 참가자 엔티티
		mockParticipant = Participant.builder()
			.participantId(participantUserId)
			.build();

		// 매핑 엔티티
		mockMapping = CompetitionParticipantMapping.builder()
			.competition(mockCompetition)
			.participant(mockParticipant)
			.build();
	}

	@DisplayName("대회 참가자 목록 조회 성공 테스트")
	@Test
	void getParticipants_Success() {
		// given
		PageRequest pageable = PageRequest.of(0, 10);
		Page<CompetitionParticipantMapping> mappingPage =
			new PageImpl<>(Collections.singletonList(mockMapping), pageable, 1);

		given(competitionRepository.findById(competitionId)).willReturn(mockCompetition);
		given(mappingRepository.findParticipants(competitionId, pageable)).willReturn(mappingPage);

		ParticipantAppResponseDto responseDto = ParticipantAppResponseDto.builder()
			.participantId(participantUserId)
			.build();
		Page<ParticipantAppResponseDto> responsePage =
			new PageImpl<>(List.of(responseDto), pageable, 1);

		given(participantMapper.toParticipantAppResponseDtoPage(mappingPage)).willReturn(responsePage);

		// when
		Page<ParticipantAppResponseDto> result = participantService.getParticipants(pageable, competitionId);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getParticipantId()).isEqualTo(participantUserId);
		then(competitionRepository).should(times(1)).findById(competitionId);
		then(mappingRepository).should(times(1)).findParticipants(competitionId, pageable);
		then(participantMapper).should(times(1)).toParticipantAppResponseDtoPage(mappingPage);
	}

	@DisplayName("대회 참가자 목록 조회 실패 테스트 - Repository 에러")
	@Test
	void getParticipants_Fail() {
		// given
		PageRequest pageable = PageRequest.of(0, 10);
		willThrow(new RuntimeException("DB error"))
			.given(competitionRepository).findById(competitionId);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> participantService.getParticipants(pageable, competitionId)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
	}

	@DisplayName("대회 참가자 검색 성공 테스트 - Title 검색")
	@Test
	void searchParticipants_Success() {
		// given
		String keyword = "testTitle";
		String searchType = "title";
		PageRequest pageable = PageRequest.of(0, 10);

		Page<Object> mockSearchResult = new PageImpl<>(Collections.singletonList(new Object()), pageable, 1);
		given(participantRepository.searchByTitle(keyword, pageable)).willReturn(mockSearchResult);

		SearchParticipantAppResponseDto mappedDto = SearchParticipantAppResponseDto.builder()
			.competitionID(competitionId)
			.participantID(participantUserId)
			.build();

		// Stub: Page<Object> -> Page<SearchParticipantAppResponseDto>
		given(participantMapper.toSearchParticipantAppResponseDto(any())).willReturn(mappedDto);

		// when
		Page<SearchParticipantAppResponseDto> result = participantService.searchParticipants(keyword, searchType, pageable);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getCompetitionID()).isEqualTo(competitionId);
		then(participantRepository).should(times(1)).searchByTitle(keyword, pageable);
		then(participantMapper).should(atLeastOnce()).toSearchParticipantAppResponseDto(any());
	}

	@DisplayName("대회 참가자 검색 실패 테스트 - 지원하지 않는 searchType")
	@Test
	void searchParticipants_Fail_InvalidSearchType() {
		// given
		String keyword = "someKeyword";
		String invalidSearchType = "invalidType"; // 유효하지 않은 검색 키워드
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> participantService.searchParticipants(keyword, invalidSearchType, pageable)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.PARTICIPANT_SEARCH_FAIL);
	}

	@DisplayName("특정 참가자의 신청 내역 조회 성공 테스트")
	@Test
	void getParticipant_Success() {
		// given
		PageRequest pageable = PageRequest.of(0, 10);
		Page<Object> mockPage = new PageImpl<>(Collections.singletonList(new Object()), pageable, 1);
		given(participantRepository.getByUuid(participantUserId, pageable)).willReturn(mockPage);

		SearchParticipantAppResponseDto mappedDto = SearchParticipantAppResponseDto.builder()
			.competitionID(competitionId)
			.participantID(participantUserId)
			.build();

		// Stub: Page<Object> -> Page<SearchParticipantAppResponseDto>
		given(participantMapper.toSearchParticipantAppResponseDto(any())).willReturn(mappedDto);

		// when
		Page<SearchParticipantAppResponseDto> result =
			participantService.getParticipant(participantUserId.toString(), pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().get(0).getCompetitionID()).isEqualTo(competitionId);
		then(participantRepository).should(times(1)).getByUuid(participantUserId, pageable);
		then(participantMapper).should(atLeastOnce()).toSearchParticipantAppResponseDto(any());
	}

	@DisplayName("특정 참가자의 신청 내역 조회 실패 테스트 - UUID 변환 불가")
	@Test
	void getParticipant_Fail_InvalidUUID() {
		// given
		String invalidUUID = "not-a-valid-uuid";
		PageRequest pageable = PageRequest.of(0, 10);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> participantService.getParticipant(invalidUUID, pageable)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.PARTICIPANT_GET_FAIL);
	}

	@DisplayName("대회 신청 취소(기업 측) 성공 테스트")
	@Test
	void cancelParticipantByCompany_Success() {
		// given
		CancelParticipantRequestDto requestDto = CancelParticipantRequestDto.builder()
			.competitionId(competitionId)
			.participantId(participantUserId)
			.build();

		given(mappingRepository.findByCompetitionIdAndParticipantId(
			competitionId, participantUserId)).willReturn(mockMapping);

		// when
		participantService.cancelParticipantByCompany(requestDto);

		// then
		assertThat(mockMapping.getStatus().name()).isEqualTo("CANCEL");
		then(mappingRepository).should(times(1))
			.findByCompetitionIdAndParticipantId(competitionId, participantUserId);
	}

	@DisplayName("대회 신청 취소(기업 측) 실패 테스트 - DB 에러")
	@Test
	void cancelParticipantByCompany_Fail() {
		// given
		CancelParticipantRequestDto requestDto = CancelParticipantRequestDto.builder()
			.competitionId(competitionId)
			.participantId(participantUserId)
			.build();

		willThrow(new RuntimeException("DB error"))
			.given(mappingRepository).findByCompetitionIdAndParticipantId(competitionId, participantUserId);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> participantService.cancelParticipantByCompany(requestDto)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
	}

	@DisplayName("대회 신청 취소(참가자 본인) 성공 테스트")
	@Test
	void cancelParticipant_Success() {
		// given
		CancelParticipantRequestDto requestDto = CancelParticipantRequestDto.builder()
			.competitionId(competitionId)
			.participantId(participantUserId)
			.build();

		given(mappingRepository.findByCompetitionIdAndParticipantId(
			competitionId, participantUserId)).willReturn(mockMapping);

		// when
		participantService.cancelParticipant(requestDto);

		// then
		assertThat(mockMapping.getStatus().name()).isEqualTo("CANCEL");
		then(mappingRepository).should(times(1))
			.findByCompetitionIdAndParticipantId(competitionId, participantUserId);
	}

	@DisplayName("대회 신청 취소(참가자 본인) 실패 테스트 - 예외 발생")
	@Test
	void cancelParticipant_Fail() {
		// given
		CancelParticipantRequestDto requestDto = CancelParticipantRequestDto.builder()
			.competitionId(competitionId)
			.participantId(participantUserId)
			.build();

		willThrow(new RuntimeException("DB error"))
			.given(mappingRepository).findByCompetitionIdAndParticipantId(competitionId, participantUserId);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> participantService.cancelParticipant(requestDto)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.PARTICIPANT_CANCEL_FAIL);
	}

}