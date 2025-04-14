package com._42195km.msa.chatbotservice.application.dto.response;

import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class SearchConversationResponseAppDto {
    private UUID id;
    private UUID userId;
    private String question;
    private String answer;

    public static SearchConversationResponseAppDto from(Conversation conversation) {
        return SearchConversationResponseAppDto.builder()
                .id(conversation.getId())
                .userId(conversation.getUserId())
                .question(conversation.getQuestion())
                .answer(conversation.getAnswer())
                .build();
    }

}
