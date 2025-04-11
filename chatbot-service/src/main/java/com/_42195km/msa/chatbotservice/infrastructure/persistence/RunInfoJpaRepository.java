package com._42195km.msa.chatbotservice.infrastructure.persistence;

import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.domain.entity.RunInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RunInfoJpaRepository extends JpaRepository<RunInfo, UUID> {

}
