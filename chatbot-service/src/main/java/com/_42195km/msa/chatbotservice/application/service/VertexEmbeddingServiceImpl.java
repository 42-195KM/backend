package com._42195km.msa.chatbotservice.application.service;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VertexEmbeddingServiceImpl implements EmbeddingService {

    private final VectorStore vectorStore;

    @Override
    public void saveEmbeddingInfo(String info) {

        List<Document> documents = List.of(new Document(info));
        vectorStore.add(documents);

    }


    @Override
    public List<Document> similaritySearch(String query) {
        return this.vectorStore.similaritySearch(SearchRequest.builder().query(query).topK(5).build());

    }
}
