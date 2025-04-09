package com._42195km.msa.chatbotservice.infrastructure.persistence;

import com._42195km.msa.chatbotservice.application.dto.request.SearchConversationAppDto;
import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.domain.entity.QConversation;
import com._42195km.msa.chatbotservice.domain.repository.ConversationRepository;
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
public class ConversationRepositoryImpl implements ConversationRepository {

    private final ConversationJpaRepository chatbotJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void save(Conversation conversation) {
        chatbotJpaRepository.save(conversation);
    }

    @Override
    public Page<Conversation> search(SearchConversationAppDto dto, Pageable pageable) {
        QConversation conversation = QConversation.conversation;

        BooleanBuilder builder = new BooleanBuilder();
        if(dto!=null){
            if(dto.getUserId()!=null){
                builder.and(conversation.userId.eq(dto.getUserId()));
            }
            if(dto.getCreatedAt()!=null){
                builder.and(conversation.createdAt.gt(dto.getCreatedAt()));
            }
        }

        QueryResults<Conversation> results = queryFactory
                .selectFrom(conversation)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(conversation.createdAt.desc())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }
}
