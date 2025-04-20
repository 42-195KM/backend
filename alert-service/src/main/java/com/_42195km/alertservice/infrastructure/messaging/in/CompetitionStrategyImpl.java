package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.AlertStrategy;
import com._42195km.alertservice.exception.AlertCode;
import com._42195km.alertservice.infrastructure.messaging.dto.CompetitionEventDto;
import com._42195km.msa.common.exception.CustomBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CompetitionStrategyImpl implements AlertStrategy<CompetitionEventDto> {
    @Override
    public String makeMessage(CompetitionEventDto eventDto) {
        return eventDto.getTitle() +" 신청이 완료되었습니다.";
    }

    @Override
    public void throwException(Exception e) {
        log.error("대회 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
        throw CustomBusinessException.from(AlertCode.COMPETITION_CONSUMER_ERROR);
    }

    @Override
    public String getMediaId(CompetitionEventDto eventDto) {
        return eventDto.getMediaId();
    }
}
