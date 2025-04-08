package com._42195km.msa.chatbotservice.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeminiService implements AiService {

    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final SseService sseService;

    @Override
    public void sendQuestion(Long userId, String question) {
        Prompt prompt = new Prompt(question);

        vertexAiGeminiChatModel
                .stream(prompt)
                .doOnNext(res -> {
                   String token = res.getResult().getOutput().getText();

                   sseService.broadcast(userId, "Gemini", token);
                })
                .doOnComplete(()->{
                    sseService.broadcast(userId, "Gemini end", "\n");
                })
                .doOnError(e -> {
                    e.printStackTrace();
                })
                .subscribe();
    }

}
