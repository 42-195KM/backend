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
            아래는 관련된 문서입니다:
            %s
    
            위 문서를 참고하여 다음 질문에 답변해주세요:
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
                    sseService.broadcast(questionDto.getUserId(), "Gemini end", "\n");
                })
                .doOnError(e -> {
                    throw CustomBusinessException.from(ChatbotCode.AI_ERROR);
                })
                .subscribe();
    }

}
