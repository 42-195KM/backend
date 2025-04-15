package com._42195km.msa.chatbotservice.domain.entity;

import com._42195km.msa.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "p_chatbot")
public class Conversation extends BaseEntity {

    @Builder
    public Conversation(UUID userId, String question, String answer) {
        this.userId = userId;
        this.question = question;
        this.answer = answer;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="user_id",nullable = false)
    private UUID userId;
    @Column(name="question",nullable = false)
    private String question;
    @Column(name="answer",nullable = false, columnDefinition = "TEXT")
    private String answer;


}
