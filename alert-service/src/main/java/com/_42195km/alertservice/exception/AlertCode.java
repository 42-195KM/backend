package com._42195km.alertservice.exception;

import com._42195km.msa.common.code.ServiceCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AlertCode implements ServiceCode {

    SUCCESS("Alert000", "성공적으로 처리되었습니다.", HttpStatus.OK),
    ALERT_ERROR("Alert001","알림 서버연결이 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    ACHIEVEMENT_CONSUMER_ERROR("Alert002","업적 이벤트 처리 중 오류 발생", HttpStatus.INTERNAL_SERVER_ERROR),
    COMPETITION_CONSUMER_ERROR("Alert003","대회 이벤트 처리 중 오류 발생:", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final String code;
    private final String message;
    private final int status;

    AlertCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status.value();
    }


}
