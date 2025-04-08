package com._42195km.msa.chatbotservice.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseService {

    private final Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = 300000L;

    public SseEmitter subscribe(Long userId){
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        sseEmitterMap.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterMap.remove(userId));
        sseEmitter.onTimeout(() -> {
            sseEmitter.complete();
            sseEmitterMap.remove(userId);
        });
        sseEmitter.onError(throwable -> {
            sseEmitter.complete();
            sseEmitterMap.remove(userId);
        });
        sendToClient(userId, "First subscribe",  "userId: " + userId + " sse 연결");
        return sseEmitter;
    }

    public void broadcast(Long userId, String eventName, Object eventData){
        sendToClient(userId, eventName,  eventData);
    }


    public void sendToClient(Long userId, String eventName, Object eventData){
        SseEmitter sseEmitter = sseEmitterMap.get(userId);
        try {
            sseEmitter.send(SseEmitter
                            .event()
                            .id(userId.toString())
                            .name(eventName)
                            .data(eventData)
                    );
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }



}
