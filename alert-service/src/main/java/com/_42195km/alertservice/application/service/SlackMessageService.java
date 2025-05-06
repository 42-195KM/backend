package com._42195km.alertservice.application.service;

import com._42195km.alertservice.exception.AlertCode;
import com._42195km.msa.common.exception.CustomBusinessException;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class SlackMessageService implements MessageService {

    @Value("${slack.token}")
    private String slackBotToken;

    @Override
    public void sendMessage(String message, String mediaId) {
        Slack slack = Slack.getInstance();

        MethodsClient methods = slack.methods(slackBotToken);

        try {
            ChatPostMessageResponse response = methods.chatPostMessage(ChatPostMessageRequest.builder()
                    .channel(mediaId)
                    .text(message)
                    .build());
            log.info("ChatPostMessageResponse: {}", response);
            if(response.isOk()) {
                MDC.put("status", "success");
                log.info("전송 완료: {}", response);
            }
            else {

                throw CustomBusinessException.from(AlertCode.ALERT_ERROR);
            }

        } catch (IOException | SlackApiException e) {
            MDC.put("status", "retry");
            log.error("Slack 서버 메시지 전송 에러 {}",e.getMessage());
            throw CustomBusinessException.from(AlertCode.ALERT_ERROR);
        }finally {
            MDC.clear();
        }

    }
}
