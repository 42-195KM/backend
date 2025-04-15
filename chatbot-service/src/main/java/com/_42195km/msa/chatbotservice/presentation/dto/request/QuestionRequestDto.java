package com._42195km.msa.chatbotservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class QuestionRequestDto {

    private UUID userId;
    private String question;




}
