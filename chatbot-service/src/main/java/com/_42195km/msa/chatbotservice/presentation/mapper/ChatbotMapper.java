package com._42195km.msa.chatbotservice.presentation.mapper;

import com._42195km.msa.chatbotservice.application.dto.request.QuestionRequestAppDto;
import com._42195km.msa.chatbotservice.application.dto.request.SearchConversationRequestAppDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.QuestionRequestDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.SearchConversationRequestDto;

public class ChatbotMapper {

    public static QuestionRequestAppDto toQuestionRequestAppDto(QuestionRequestDto questionRequestDto) {
        return QuestionRequestAppDto.builder()
                .question(questionRequestDto.getQuestion())
                .userId(questionRequestDto.getUserId())
                .build();
    }


}
