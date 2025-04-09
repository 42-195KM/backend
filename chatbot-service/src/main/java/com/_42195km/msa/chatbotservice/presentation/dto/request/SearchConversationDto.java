package com._42195km.msa.chatbotservice.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class SearchConversationDto {

    private UUID userId;
    private LocalDateTime createdAt;

}
