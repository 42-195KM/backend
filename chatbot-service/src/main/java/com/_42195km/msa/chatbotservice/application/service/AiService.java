package com._42195km.msa.chatbotservice.application.service;

import com._42195km.msa.chatbotservice.application.dto.request.QuestionRequestAppDto;
import com._42195km.msa.chatbotservice.presentation.dto.request.QuestionRequestDto;

public interface AiService {

    void sendQuestion(QuestionRequestAppDto questionRequestAppDto);

}
