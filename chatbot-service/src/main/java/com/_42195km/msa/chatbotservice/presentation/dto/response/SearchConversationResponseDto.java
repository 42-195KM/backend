package com._42195km.msa.chatbotservice.presentation.dto.response;

import com._42195km.msa.chatbotservice.application.dto.request.SearchConversationRequestAppDto;
import lombok.*;

import java.util.UUID;

@Builder(access = AccessLevel.PROTECTED)
@Getter
public class SearchConversationResponseDto {
    private UUID id;
    private UUID userId;
    private String question;
    private String answer;

    public static SearchConversationResponseDto create(UUID id, UUID userId, String question, String answer) {
        return SearchConversationResponseDto.builder()
                .id(id)
                .userId(userId)
                .question(question)
                .answer(answer)
                .build();
    }

}
