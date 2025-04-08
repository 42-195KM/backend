package com._42195km.msa.chatbotservice.application.service;

import com._42195km.msa.chatbotservice.application.dto.request.QuestionRequestAppDto;
import com._42195km.msa.chatbotservice.exception.code.ChatbotCode;
import com._42195km.msa.chatbotservice.infrastructure.sse.SseServiceImpl;
import com._42195km.msa.common.exception.CustomBusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeminiService implements AiService {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final SseServiceImpl sseService;

    @Override
    public void sendQuestion(QuestionRequestAppDto questionDto) {
        Prompt prompt = new Prompt(questionDto.getQuestion());

        vertexAiGeminiChatModel
                .stream(prompt)
                .doOnNext(res -> {
                   String token = res.getResult().getOutput().getText();

                   sseService.broadcast(questionDto.getUserId(), "Gemini", token);
                })
                .doOnComplete(()->{
                    sseService.broadcast(questionDto.getUserId(), "Gemini end", "\n");
                })
                .doOnError(e -> {
                    throw CustomBusinessException.from(ChatbotCode.AI_ERROR);
                })
                .subscribe();
    }

}
