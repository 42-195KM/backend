package com._42195km.msa.chatbotservice.application.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface SseService {
    SseEmitter subscribe(UUID userId);
    void broadcast(UUID userId, String eventName, Object eventData);
    void sendToClient(UUID userId, String eventName, Object eventData);
}
