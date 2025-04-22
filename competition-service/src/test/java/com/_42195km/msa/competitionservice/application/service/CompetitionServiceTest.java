package com._42195km.msa.competitionservice.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

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
import org.springframework.test.context.ActiveProfiles;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.competitionservice.application.dto.request.CreateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.request.UpdateCompetitionCommandDto;
import com._42195km.msa.competitionservice.application.dto.response.CompetitionAppResponseDto;
import com._42195km.msa.competitionservice.application.exception.CompetitionServiceCode;
import com._42195km.msa.competitionservice.application.mapper.CompetitionMapper;
import com._42195km.msa.competitionservice.domain.model.Competition;
import com._42195km.msa.competitionservice.domain.model.CompetitionType;
import com._42195km.msa.competitionservice.domain.model.ReceptionType;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionParticipantMappingRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.CompetitionRepositoryImpl;
import com._42195km.msa.competitionservice.infrastructure.persistence.ParticipantRepositoryImpl;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CompetitionServiceTest {
	@InjectMocks
	private CompetitionService competitionService;

	@Mock
	private CompetitionRepositoryImpl competitionRepository;

	@Mock
	private ParticipantRepositoryImpl participantRepository;

	@Mock
	private CompetitionParticipantMappingRepositoryImpl mappingRepository;

	@Mock
	private CompetitionMapper competitionMapper;

	private UUID userId;
	private UUID competitionId;
	private Competition mockCompetition;

	@BeforeEach
	void setUp() {
		userId = UUID.randomUUID();
		competitionId = UUID.randomUUID();

		mockCompetition = Competition.builder()
			.userId(userId)
			.title("테스트 대회")
			.type(CompetitionType.KM10)
			.receptionType(ReceptionType.FIRST)
			.participantsNum(100)
			.price(10000)
			.build();
	}

	@DisplayName("대회 생성 성공 테스트")
	@Test
	void createCompetition_Success() {
		// given
		CreateCompetitionCommandDto command =
			new CreateCompetitionCommandDto(
				userId, "테스트 대회", CompetitionType.KM10, ReceptionType.FIRST, 100, 10000
			);

		// 예외 없이 성공적으로 동작한다고 가정하므로, 특별히 when-stub 설정하지 않음

		// when
		competitionService.createCompetition(command);

		// then
		then(competitionRepository)
			.should(times(1))
			.save(any(Competition.class));
	}

	@DisplayName("대회 생성 실패 테스트 - 저장 과정에서 예외 발생")
	@Test
	void createCompetition_Fail() {
		// given
		CreateCompetitionCommandDto command =
			new CreateCompetitionCommandDto(
				userId, "테스트 대회", CompetitionType.KM10, ReceptionType.FIRST, 100, 10000
			);
		willThrow(new RuntimeException("DB Error"))
			.given(competitionRepository).save(any(Competition.class));

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> competitionService.createCompetition(command)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.COMPETITION_CREATE_FAIL);
	}

	@DisplayName("대회 목록 조회 성공 테스트")
	@Test
	void getCompetitions_Success() {
		// given
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Competition> pageResult = new PageImpl<>(java.util.List.of(mockCompetition), pageRequest, 1);
		given(competitionRepository.findAll(pageRequest)).willReturn(pageResult);

		CompetitionAppResponseDto mockDto = new CompetitionAppResponseDto(
			competitionId, userId, "테스트 대회", CompetitionType.KM10, ReceptionType.FIRST, 100, 10000
		);
		given(competitionMapper.toAppResponseDtoPage(pageResult))
			.willReturn(new PageImpl<>(java.util.List.of(mockDto), pageRequest, 1));

		// when
		Page<CompetitionAppResponseDto> result = competitionService.getCompetitions(pageRequest);

		// then
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 대회");
		then(competitionRepository).should(times(1)).findAll(pageRequest);
	}

	@DisplayName("대회 단건 조회 성공 테스트")
	@Test
	void getCompetition_Success() {
		// given
		given(competitionRepository.findById(competitionId)).willReturn(mockCompetition);
		CompetitionAppResponseDto mockDto = new CompetitionAppResponseDto(
			competitionId, userId, "테스트 대회", CompetitionType.KM10, ReceptionType.FIRST, 100, 10000
		);
		given(competitionMapper.toAppResponseDto(mockCompetition)).willReturn(mockDto);

		// when
		CompetitionAppResponseDto result = competitionService.getCompetition(competitionId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("테스트 대회");
		then(competitionRepository).should(times(1)).findById(competitionId);
		then(competitionMapper).should(times(1)).toAppResponseDto(mockCompetition);
	}

	@DisplayName("대회 단건 조회 실패 테스트 - 해당 ID의 대회 없음")
	@Test
	void getCompetition_NotFound() {
		// given
		willThrow(CustomBusinessException.from(CompetitionServiceCode.COMPETITION_CREATE_FAIL))
			.given(competitionRepository).findById(competitionId);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> competitionService.getCompetition(competitionId)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.COMPETITION_GET_ID_FAIL);
	}

	@DisplayName("대회 수정 성공 테스트")
	@Test
	void updateCompetition_Success() {
		// given
		UpdateCompetitionCommandDto updateCommand = new UpdateCompetitionCommandDto(
			"변경된 대회 제목", CompetitionType.HALF, ReceptionType.DRAW, 200, 15000
		);
		given(competitionRepository.findById(competitionId)).willReturn(mockCompetition);

		// when
		competitionService.updateCompetition(competitionId, updateCommand);

		// then
		assertThat(mockCompetition.getTitle()).isEqualTo("변경된 대회 제목");
		assertThat(mockCompetition.getType()).isEqualTo(CompetitionType.HALF);
		assertThat(mockCompetition.getReceptionType()).isEqualTo(ReceptionType.DRAW);
		assertThat(mockCompetition.getParticipantsNum()).isEqualTo(200);
		assertThat(mockCompetition.getPrice()).isEqualTo(15000);
	}

	@DisplayName("대회 수정 실패 테스트 - 대회 조회 실패")
	@Test
	void updateCompetition_Fail() {
		// given
		UpdateCompetitionCommandDto updateCommand = new UpdateCompetitionCommandDto(
			"변경된 대회 제목", CompetitionType.HALF, ReceptionType.DRAW, 200, 15000
		);
		willThrow(new RuntimeException("DB Error"))
			.given(competitionRepository).findById(competitionId);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> competitionService.updateCompetition(competitionId, updateCommand)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.COMPETITION_UPDATE_FAIL);
	}

	//@DisplayName("대회 삭제 성공 테스트")
	//@Test
	//void deleteCompetition_Success() {
		// given
	//	given(competitionRepository.findById(competitionId)).willReturn(mockCompetition);

		// when
	//	competitionService.deleteCompetition(competitionId);

		// then
		// "삭제" 라는 구현이 실제론 setDeleted() 형태로 소프트 딜리트 등을 가정
		// 별도의 예외가 발생하지 않으면 성공으로 가정
	//	then(competitionRepository).should(times(1)).findById(competitionId);
	//}

	@DisplayName("대회 삭제 실패 테스트 - 대회 조회 실패")
	@Test
	void deleteCompetition_Fail() {
		// given
		willThrow(new RuntimeException("DB Error"))
			.given(competitionRepository).findById(competitionId);

		// when
		CustomBusinessException ex = assertThrows(
			CustomBusinessException.class,
			() -> competitionService.deleteCompetition(competitionId)
		);

		// then
		assertThat(ex.getCode()).isEqualTo(CompetitionServiceCode.COMPETITION_UPDATE_FAIL);
	}
}