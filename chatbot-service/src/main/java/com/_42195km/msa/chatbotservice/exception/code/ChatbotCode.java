package com._42195km.msa.chatbotservice.exception.code;

import com._42195km.msa.common.code.ServiceCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatbotCode implements ServiceCode {

    SUCCESS("CHAT_000", "성공적으로 처리되었습니다.", HttpStatus.OK),
    AI_ERROR("CHAT001","생성형AI 서버연결이 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    SSE_ERROR("CHAT002","Sse 연결이 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),



    ;

    private final String code;
    private final String message;
    private final int status;

    ChatbotCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status.value();
    }


}
