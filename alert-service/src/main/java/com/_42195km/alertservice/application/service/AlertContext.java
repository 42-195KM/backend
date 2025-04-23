package com._42195km.alertservice.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AlertContext<T> {

    private final ObjectMapper objectMapper;
    private final MessageService messageService;

    public void sendMessage(Map<String, Object> eventMap, AlertStrategy<T> alertStrategy, Class<T> clazz) {
        try {
            T dto = objectMapper.convertValue(eventMap, clazz);

            String message = alertStrategy.makeMessage(dto);
            messageService.sendMessage(message, alertStrategy.getMediaId(dto));
        }catch (Exception e) {
            alertStrategy.throwException(e);
        }
    }

}
