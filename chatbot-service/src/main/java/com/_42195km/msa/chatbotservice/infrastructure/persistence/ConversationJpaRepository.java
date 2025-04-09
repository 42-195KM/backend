package com._42195km.msa.chatbotservice.infrastructure.persistence;

import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationJpaRepository extends JpaRepository<Conversation, UUID> {

}
