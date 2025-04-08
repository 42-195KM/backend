package com._42195km.msa.chatbotservice.infrastructure.persistence;

import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.domain.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConversationRepositoryImpl implements ConversationRepository {

    private final ConversationJpaRepository chatbotJpaRepository;

    @Override
    public void save(Conversation conversation) {
        chatbotJpaRepository.save(conversation);
    }

    @Override
    public Page<Conversation> search(Conversation conversation, Pageable pageable) {
        return null;
    }
}
