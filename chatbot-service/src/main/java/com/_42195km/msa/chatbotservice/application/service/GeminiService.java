package com._42195km.msa.chatbotservice.application.service;

import com._42195km.msa.chatbotservice.application.dto.request.QuestionRequestAppDto;
import com._42195km.msa.chatbotservice.domain.entity.Conversation;
import com._42195km.msa.chatbotservice.domain.repository.ConversationRepository;
import com._42195km.msa.chatbotservice.exception.code.ChatbotCode;
import com._42195km.msa.chatbotservice.infrastructure.sse.SseServiceImpl;
import com._42195km.msa.common.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiService implements AiService {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final SseServiceImpl sseService;
    private final ConversationRepository conversationRepository;
    private final EmbeddingService embeddingService;

    @Override
    public void sendQuestion(QuestionRequestAppDto questionDto) {
        StringBuffer stringBuffer = new StringBuffer();

        List<Document> documents = embeddingService.similaritySearch(questionDto.getQuestion());

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        String fullPrompt = String.format("""
            당신은 마라톤 선수 출신이며 육상 전공자로서, 생활체육인들에게 마라톤 트레이닝을 전문적으로 지도해온 경험이 풍부한 전문가입니다. 아래 질문에 대해 마라톤 실전 경험과 전문 지식을 바탕으로 현실적이고 친근한 표현으로 조언을 제공하세요. 자기소개는 생략합니다.
     
            아래의 정보는 당신이 가지고 있는 전문 지식입니다. 이를 바탕으로 300자이내로 최대한 필요한 정보만 요약하여 코칭을 정확히 해주세요.
            %s
    
            다음 질문에 답변해주세요:
            %s
        """, context, questionDto.getQuestion());
        log.info("context: {}", context);
        log.info("fullPrompt: {}", fullPrompt);

        Prompt prompt = new Prompt(fullPrompt);
        vertexAiGeminiChatModel
                .stream(prompt)
                .doOnNext(res -> {
                   String token = res.getResult().getOutput().getText();
                   stringBuffer.append(token);

                   sseService.broadcast(questionDto.getUserId(), "Gemini", token);
                })
                .doOnComplete(()->{
                    conversationRepository.save(Conversation.builder()
                            .userId(questionDto.getUserId())
                            .question(questionDto.getQuestion())
                            .answer(stringBuffer.toString())
                            .build());
                    log.info("답변: {}", stringBuffer.toString());
                    sseService.broadcast(questionDto.getUserId(), "Gemini end", "\n");
                })
                .doOnError(e -> {
                    throw CustomBusinessException.from(ChatbotCode.AI_ERROR);
                })
                .subscribe();
    }

}
