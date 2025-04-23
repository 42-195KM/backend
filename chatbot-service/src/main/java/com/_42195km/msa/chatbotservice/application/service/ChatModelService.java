package com._42195km.msa.chatbotservice.application.service;

import com._42195km.msa.chatbotservice.application.dto.request.QuestionRequestAppDto;

public interface ChatModelService {

    void sendQuestion(QuestionRequestAppDto questionRequestAppDto);

}
