package com._42195km.msa.chatbotservice.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class QuestionRequestAppDto {
    private UUID userId;
    private String question;

}
