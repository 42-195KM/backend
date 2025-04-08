package com._42195km.msa.chatbotservice.presentation.mapper;

import com._42195km.msa.chatbotservice.application.dto.request.QuestionRequestAppDto;
import com._42195km.msa.chatbotservice.application.dto.request.SearchConversationAppDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.QuestionRequestDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.SearchConversationDto;

public class ChatbotMapper {

    public static QuestionRequestAppDto toQuestionRequestAppDto(QuestionRequestDto questionRequestDto) {
        return QuestionRequestAppDto.builder()
                .question(questionRequestDto.getQuestion())
                .userId(questionRequestDto.getUserId())
                .build();
    }

    public static SearchConversationAppDto toSearchConversationAppDto(SearchConversationDto searchConversationDto) {
        return SearchConversationAppDto.builder()
                .userId(searchConversationDto.getUserId())
                .createdAt(searchConversationDto.getCreatedAt())
                .build();
    }
}
