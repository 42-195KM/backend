package com._42195km.msa.chatbotservice.infrastructure.sse;

import com._42195km.msa.chatbotservice.application.service.SseService;
import com._42195km.msa.chatbotservice.exception.code.ChatbotCode;
import com._42195km.msa.common.exception.CustomBusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseServiceImpl implements SseService {

    private final Map<UUID, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = 30000L;

    public SseEmitter subscribe(UUID userId){

        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        if(!sseEmitterMap.containsKey(userId)){
            sseEmitterMap.put(userId, sseEmitter);
        }

        sseEmitter.onCompletion(() -> sseEmitterMap.remove(userId));
        sseEmitter.onTimeout(() -> {
            sseEmitterMap.get(userId).complete();
        });
        sseEmitter.onError(throwable -> {
            sseEmitterMap.get(userId).complete();
        });
        sendToClient(userId, "First subscribe",  "userId: " + userId + " sse 연결");
        return sseEmitter;
    }

    public void broadcast(UUID userId, String eventName, Object eventData){
        sendToClient(userId, eventName,  eventData);
    }


    public void sendToClient(UUID userId, String eventName, Object eventData){
        SseEmitter sseEmitter = sseEmitterMap.get(userId);
        try {
            sseEmitter.send(SseEmitter
                            .event()
                            .id(userId.toString())
                            .name(eventName)
                            .data(eventData)
                    );
        } catch (IOException e) {
            throw CustomBusinessException.from(ChatbotCode.SSE_ERROR);
        }

    }



}
