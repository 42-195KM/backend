package com._42195km.alertservice.infrastructure.messaging.in;

import com._42195km.alertservice.application.service.AlertStrategy;
import com._42195km.alertservice.exception.AlertCode;
import com._42195km.alertservice.infrastructure.messaging.dto.AchieveEventDto;
import com._42195km.msa.common.exception.CustomBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AchievementStrategyImpl implements AlertStrategy<AchieveEventDto> {


    @Override
    public String makeMessage(AchieveEventDto eventDto) {
        return "축하합니다! " + eventDto.getAchievementTitle() +" 업적을 달성하였습니다!!";
    }

    @Override
    public void throwException(Exception e) {
        log.error("업적 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
        throw CustomBusinessException.from(AlertCode.ACHIEVEMENT_CONSUMER_ERROR);
    }

    @Override
    public String getMediaId(AchieveEventDto eventDto) {
        return eventDto.getUserMediaId();
    }
}
