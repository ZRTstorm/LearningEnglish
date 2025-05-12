package com.eng.spring_server.service;

import com.eng.spring_server.domain.contents.VectorContent;
import com.eng.spring_server.repository.VectorContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final TextOperationService textOperationService;
    private final VectorContentRepository vectorContentRepository;

    public float[] getEmbeddingVector(String contentType, Long contentId) {
        // Get Summary Text for Content
        String summaryText = textOperationService.getSummaryText(contentType, contentId);

        EmbeddingResponse embResponse = embeddingModel.embedForResponse(List.of(summaryText));

        return embResponse.getResult().getOutput();
    }

    public void saveVector(String contentType, Long contentId) {
        // Vector Value Checking
        Optional<VectorContent> vectorOpt = vectorContentRepository.findByContentTypeAndContentId(contentType, contentId);
        if (vectorOpt.isPresent()) {
            log.info("Vector Content is Already in Store : {}", contentId);
            throw new IllegalStateException("Vector Already Stored");
        }
        // Get Summary Text for Content
        String summaryText = textOperationService.getSummaryText(contentType, contentId);

        Map<String, Object> metaData = new HashMap<>();
        metaData.put("contentType", contentType);
        metaData.put("contentId", String.valueOf(contentId));

        List<Document> documents = List.of(new Document(summaryText, metaData));
        vectorStore.add(documents);

        VectorContent content = new VectorContent();
        content.setContentType(contentType);
        content.setContentId(contentId);
        vectorContentRepository.save(content);
    }

    public List<Document> searchVector(String contentType, Long contentId) {
        // Get Summary Text for Content
        String summaryText = textOperationService.getSummaryText(contentType, contentId);

        FilterExpressionBuilder b = new FilterExpressionBuilder();

        return vectorStore.similaritySearch(SearchRequest.builder().query(summaryText)
                .topK(2)
                .filterExpression(b.eq("contentType", contentType).build()).build());
    }
}
