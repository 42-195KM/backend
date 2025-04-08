package com._42195km.msa.chatbotservice.domain.repository;

import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConversationRepository {

    void save(Conversation conversation);

    Page<Conversation> search(Conversation conversation, Pageable pageable);




}
