package com._42195km.msa.chatbotservice.infrastructure.persistence;

import com._42195km.msa.chatbotservice.application.dto.request.SearchConversationAppDto;
import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.domain.entity.QConversation;
import com._42195km.msa.chatbotservice.domain.entity.RunInfo;
import com._42195km.msa.chatbotservice.domain.repository.ConversationRepository;
import com._42195km.msa.chatbotservice.domain.repository.RunInfoRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RunInfoRepositoryImpl implements RunInfoRepository {

    private final RunInfoJpaRepository runInfoJpaRepository;


    @Override
    public RunInfo save(RunInfo runInfo) {
        runInfoJpaRepository.save(runInfo);
        return runInfo;
    }

}
