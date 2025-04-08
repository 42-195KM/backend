package com._42195km.msa.chatbotservice.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
public class SearchConversationAppDto {

    private UUID userId;
    private LocalDateTime createdAt;


}
