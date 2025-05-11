package com.eng.spring_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final TextOperationService textOperationService;

    public float[] getEmbeddingVector(String contentType, Long contentId) {
        // Get Summary Text for Content
        String summaryText = textOperationService.getSummaryText(contentType, contentId);

        EmbeddingResponse embResponse = embeddingModel.embedForResponse(List.of(summaryText));

        return embResponse.getResult().getOutput();
    }
}
