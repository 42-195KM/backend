package com._42195km.msa.chatbotservice.application.service;

import org.springframework.ai.document.Document;

import java.util.List;

public interface EmbeddingService {
    void saveEmbeddingInfo(String info);
    List<Document> similaritySearch(String query);

}
