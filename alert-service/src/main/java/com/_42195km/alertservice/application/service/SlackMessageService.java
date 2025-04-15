package com._42195km.alertservice.application.service;

import com._42195km.alertservice.code.AlertCode;
import com._42195km.msa.common.exception.CustomBusinessException;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsOpenRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsOpenResponse;
import com.slack.api.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

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
            if (response.isOk()) {
                Message postedMessage = response.getMessage();
            } else {
                String errorCode = response.getError(); // e.g., "invalid_auth", "channel_not_found"
                throw CustomBusinessException.from(AlertCode.ALERT_ERROR);
            }


        } catch (IOException | SlackApiException e) {
            throw CustomBusinessException.from(AlertCode.ALERT_ERROR);
        }

    }
}
