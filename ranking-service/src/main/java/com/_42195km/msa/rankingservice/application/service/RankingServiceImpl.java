package com._42195km.msa.rankingservice.application.service;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com._42195km.msa.common.exception.CustomBusinessException;
import com._42195km.msa.rankingservice.application.dto.response.CreatePersonalRanking;
import com._42195km.msa.rankingservice.application.dto.response.GetAllPersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.dto.response.GetPersonalRankingResponseDto;
import com._42195km.msa.rankingservice.application.dto.response.RankingDetailResponseDto;
import com._42195km.msa.rankingservice.application.exception.RankingException;
import com._42195km.msa.rankingservice.domain.model.DividePersonal;
import com._42195km.msa.rankingservice.domain.model.DomainType;
import com._42195km.msa.rankingservice.domain.model.Ranking;
import com._42195km.msa.rankingservice.domain.model.RankingDetail;
import com._42195km.msa.rankingservice.infrastructure.messaging.RunningRecordServiceClient;
import com._42195km.msa.rankingservice.infrastructure.persistence.RankingDetailRepositoryImpl;
import com._42195km.msa.rankingservice.infrastructure.persistence.RankingRepositoryImpl;
import com._42195km.msa.rankingservice.presentation.dto.response.RunningRecordResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

	private final static String DOMAIN_TYPE_USER = "USER";
	private final static String DOMAIN_TYPE_CREW = "CREW";

	private final RankingRepositoryImpl rankingRepositoryImpl;
	private final RankingDetailRepositoryImpl rankingDetailRepositoryImpl;
	private final RunningRecordServiceClient runningRecordServiceClient;
	private final HttpServletRequest request;

	@Override
	@Transactional
	public CreatePersonalRanking createPersonalRanking() {

		String header = request.getHeader("Authorization");

		// 전체 러닝 기록 조회해오기
		log.info("=========개인 랭킹 생성 시작=========");
		List<RunningRecordResponseDto.RunningRecordData> allRecords = getAllRecord(header);

		// 집계 -> 한 유저가 여러개의 데이터가 있을수 있으므로 그 데이터들의 평균으로 등수를 낸다.(현재 사용중인 방법)
		// 고려할점 : 그럼 데이터를 불러올때 , 날짜 구간이 있어야함 or 그냥 과거~현재까지의 모든 데이터를 가져와서 평균을 내야함?
		Map<UUID, DividePersonal> divide = divideRecordByUser(allRecords);

		log.info("집계된 유저별 데이터: {}", divide);

		// TODO : 데이터가 많아지면 ? bulk insert 고려
		// 랭킹 산정 후 DB에 저장 -> saveall
		startToRanking(divide);
		log.info("=========개인 랭킹 생성 종료=========");

		return CreatePersonalRanking.builder().build();
	}

	@Override
	public Page<GetAllPersonalRankingResponseDto> getAllrankings(Pageable pageable) {

		Page<Ranking> rankings = rankingRepositoryImpl.findAllWithDetails(pageable);

		if (rankings.isEmpty()) {
			throw CustomBusinessException.from(RankingException.RANKING_EMPTY);
		}

		Page<GetAllPersonalRankingResponseDto> rankingPage = rankings.map(
			GetAllPersonalRankingResponseDto::fromRanking
		);

		return rankingPage;
	}

	@Override
	public GetPersonalRankingResponseDto getRanking(UUID identifierId) {

		Ranking ranking = rankingRepositoryImpl.findWithDetails(identifierId)
			.orElseThrow(() -> CustomBusinessException.from(RankingException.NOT_FOUND_PERSONAL_RANKING));

		return GetPersonalRankingResponseDto.from(ranking);
	}

	@Override
	public Page<RankingDetailResponseDto> searchRankings(String keyword, Pageable pageable) {

		Page<RankingDetail> keywordRankings = rankingDetailRepositoryImpl.findByMetricNameOrderByRank(keyword,
			pageable);

		if (keywordRankings.isEmpty()) {
			throw CustomBusinessException.from(RankingException.RANKING_EMPTY);
		}

		Page<RankingDetailResponseDto> keywordRankingPage = keywordRankings.map(
			RankingDetailResponseDto::from
		);

		return keywordRankingPage;
	}

	@Override
	@Transactional
	public void deleteRanking(UUID individualRankingId) {

		Ranking targetRanking = rankingRepositoryImpl.findByUserId(individualRankingId)
			.orElseThrow(() -> CustomBusinessException.from(RankingException.NOT_FOUND_PERSONAL_RANKING));

		List<RankingDetail> rankingDetails = targetRanking.getDetails();

		for (RankingDetail rankingDetail : rankingDetails) {
			rankingDetail.setDeleted();
		}

		targetRanking.setDeleted();
	}

	private List<RunningRecordResponseDto.RunningRecordData> getAllRecord(String header) {

		List<RunningRecordResponseDto.RunningRecordData> allRecords = new ArrayList<>();
		int page = 0;
		boolean isLast = false;

		while (!isLast) {
			RunningRecordResponseDto response = runningRecordServiceClient.getAllRunningRecords(header, page++);
			allRecords.addAll(response.getData().getContent());
			isLast = response.getData().isLast();
		}

		log.info("전체 러닝 기록 수집 완료 - 총 {}개", allRecords.size());
		return allRecords;
	}

	private Map<UUID, DividePersonal> divideRecordByUser(List<RunningRecordResponseDto.RunningRecordData> allRecords) {

		return allRecords.stream()
			.collect(
				Collectors.groupingBy(
					// 유저UUID로 그룹화
					RunningRecordResponseDto.RunningRecordData::getUserId,
					Collectors.collectingAndThen(
						// 리스트로 모아서 평균 계산
						Collectors.toList(),
						this::calculateAvgUserData
					)
				)
			);
	}

	/// calculateAvgUserData -> 한 유저에 대해 여러 데이터가 있을때, 계산해서 객체로 리턴해주는 메소드
	private DividePersonal calculateAvgUserData(
		List<RunningRecordResponseDto.RunningRecordData> runningRecordData
	) {

		UUID targetUserId = runningRecordData.get(0).getUserId();

		double totalDistance = runningRecordData.stream()
			.mapToDouble(RunningRecordResponseDto.RunningRecordData::getDistance)
			.sum();

		Duration totalTimer = runningRecordData.stream()
			.map(timer -> Duration.parse(String.valueOf(timer.getTimer())))
			// Duration 타입 데이터 파싱해서 기본값 0 , 기본값이 아니면 더하기
			.reduce(Duration.ZERO, Duration::plus);

		double avgPace = runningRecordData.stream()
			.mapToDouble(RunningRecordResponseDto.RunningRecordData::getPace)
			.average()
			.orElse(0.0);

		return DividePersonal.builder()
			.userId(targetUserId)
			.totalDistance(totalDistance)
			.totalTimer(totalTimer)
			.avgPace(avgPace)
			.build();
	}

	private void startToRanking(Map<UUID, DividePersonal> divide) {

		String[] fieldNames = extractFieldNameInClass();
		log.info("fieldNames: {}", Arrays.toString(fieldNames));

		// 유저별 랭킹 객체 리스트
		List<Ranking> rankings = divide.entrySet().stream()
			.map(user -> Ranking.builder()
				.identifierId(user.getKey())
				.domainType(DomainType.valueOf(DOMAIN_TYPE_USER))
				.details(new ArrayList<>())
				.build())
			.collect(Collectors.toList()
			);

		// 메트릭별 랭킹 계산
		for (String metric : fieldNames) {

			SortedSet<RankingDetail> rankingDetailsSet;

			// 페이스는 오름차순 , 나머지 조건에 대해선 내림차순으로 랭킹
			if ("avgPace".equals(metric)) {
				rankingDetailsSet = new TreeSet<>(
					Comparator.comparingDouble(
						div -> Double.parseDouble(
							div.getMetricValue()
						)
					)
				);
			} else {
				rankingDetailsSet = new TreeSet<>(
					Comparator.comparingDouble(
						div -> -Double.parseDouble(
							div.getMetricValue()
						)
					)
				);
			}

			// 한 유저의 랭킹의 각 metric별 랭킹 디테일
			for (Ranking ranking : rankings) {

				UUID userId = ranking.getIdentifierId();
				DividePersonal personal = divide.get(userId);
				String metricValue = extractFieldValue(personal, metric);

				RankingDetail detail = RankingDetail.builder()
					.ranking(ranking)
					.metricName(metric)
					.metricValue(metricValue)
					// 랭킹 초기값 0
					.rank(0)
					.date(LocalDate.now())
					.build();

				rankingDetailsSet.add(detail);
			}

			// 랭킹설정
			int rank = 1;
			for (RankingDetail detail : rankingDetailsSet) {
				detail.applyRanking(rank++);
				detail.getRanking().getDetails().add(detail);
			}
		}

		rankingRepositoryImpl.saveAll(rankings);
	}

	// metric필드명으로 값 뽑아오기
	private String extractFieldValue(DividePersonal personal, String metric) {
		try {
			Field field = personal.getClass().getDeclaredField(metric);
			field.setAccessible(true);

			// Duration 타입일경우 예외
			if (field.getType().equals(Duration.class)) {
				Duration duration = (Duration)field.get(personal);
				return String.valueOf(duration.toSeconds());
			}

			return field.get(personal).toString();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	// DividePersonal 클래스의 필드명으로 랭킹을 저장할거임 ( 단 , UUID인 userId는 제외 )
	private String[] extractFieldNameInClass() {

		Field[] fields = DividePersonal.class.getDeclaredFields();

		String[] fieldNames = Arrays.stream(fields)
			.map(Field::getName)
			// userId는 제외
			.filter(fieldName -> !fieldName.equals("userId"))
			.toArray(String[]::new);

		return fieldNames;
	}

}
