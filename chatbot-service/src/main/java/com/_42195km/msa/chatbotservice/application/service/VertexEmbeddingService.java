package com._42195km.msa.chatbotservice.application.service;

import com._42195km.msa.chatbotservice.domain.entity.RunInfo;
import com._42195km.msa.chatbotservice.domain.repository.RunInfoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VertexEmbeddingService implements EmbeddingService {

    private final EmbeddingModel embedding;
    private final RunInfoRepository runInfoRepository;

    @Override
    public float[] embeddingInfo(String info) {
        float[] embed = embedding.embed(info);

        runInfoRepository.save(RunInfo.builder()
                        .embeddingInfo(embed)
                        .build());
        return embed;
    }
}
