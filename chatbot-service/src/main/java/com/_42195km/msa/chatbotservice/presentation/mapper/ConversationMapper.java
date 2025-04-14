package com._42195km.msa.chatbotservice.presentation.mapper;

import com._42195km.msa.chatbotservice.application.dto.request.SearchConversationRequestAppDto;
import com._42195km.msa.chatbotservice.application.dto.response.SearchConversationResponseAppDto;
import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.presentation.dto.request.SearchConversationRequestDto;
import com._42195km.msa.chatbotservice.presentation.dto.response.SearchConversationResponseDto;

public class ConversationMapper {

    public static SearchConversationRequestAppDto toAppDto(SearchConversationRequestDto searchConversationRequestDto){
        return SearchConversationRequestAppDto.builder()
                .userId(searchConversationRequestDto.getUserId())
                .createdAt(searchConversationRequestDto.getCreatedAt())
                .build();
    }

    public static SearchConversationResponseDto toDto(SearchConversationResponseAppDto appDto){
        return SearchConversationResponseDto.create(appDto.getId(), appDto.getUserId(), appDto.getQuestion(), appDto.getAnswer());
    }


}
