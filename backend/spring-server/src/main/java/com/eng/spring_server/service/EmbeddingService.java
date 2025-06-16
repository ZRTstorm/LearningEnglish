package com.eng.spring_server.service;

import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.VectorContent;
import com.eng.spring_server.domain.contents.VideoContents;
import com.eng.spring_server.dto.ContentIdDto;
import com.eng.spring_server.repository.ContentsLibraryRepository;
import com.eng.spring_server.repository.TextContentsRepository;
import com.eng.spring_server.repository.VectorContentRepository;
import com.eng.spring_server.repository.VideoContentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final TextOperationService textOperationService;
    private final VectorContentRepository vectorContentRepository;
    private final VideoContentsRepository videoContentsRepository;
    private final TextContentsRepository textContentsRepository;
    private final ContentsLibraryRepository contentsLibraryRepository;

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

    public List<ContentIdDto> similarVector(Long userId, float start, float end, ContentIdDto request, String option) {
        String summaryText = textOperationService.getSummaryText(request.getContentType(), request.getContentId());

        FilterExpressionBuilder b = new FilterExpressionBuilder();

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(summaryText)
                .topK(10)
                .filterExpression(b.eq("contentType", "video").build()).build());

        List<ContentIdDto> idList = new ArrayList<>();
        for (int i = 1; i < documents.size(); i++) {
            String contentType = documents.get(i).getMetadata().get("contentType").toString();
            String contentId = documents.get(i).getMetadata().get("contentId").toString();

            if (option.equalsIgnoreCase("library")) {
                if (checkLibrary(contentType, Long.parseLong(contentId), userId)) continue;
            }

            if (checkLevel(contentType, Long.parseLong(contentId), start, end)) {
                idList.add(new ContentIdDto(contentType, Long.parseLong(contentId)));
            }
        }

        return idList;
    }

    public List<ContentIdDto> searchText(Long userId, float start, float end, String text, String option) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();

        List<Document> documents = vectorStore.similaritySearch(SearchRequest.builder().query(text)
                .topK(10)
                .filterExpression(b.eq("contentType", "video").build()).build());

        List<ContentIdDto> idList = new ArrayList<>();
        for (int i = 1; i < documents.size(); i++) {
            String contentType = documents.get(i).getMetadata().get("contentType").toString();
            String contentId = documents.get(i).getMetadata().get("contentId").toString();

            if (option.equalsIgnoreCase("library")) {
                if (checkLibrary(contentType, Long.parseLong(contentId), userId)) continue;
            }

            if (checkLevel(contentType, Long.parseLong(contentId), start, end)) {
                idList.add(new ContentIdDto(contentType, Long.parseLong(contentId)));
            }
        }

        return idList;
    }

    private boolean checkLibrary(String contentType, Long contentId, Long userId) {
        if (contentType.equalsIgnoreCase("video")) {
            VideoContents content = videoContentsRepository.getReferenceById(contentId);
            Optional<ContentsLibrary> opt = contentsLibraryRepository.findByVideoContentsAndUsers_Id(content, userId);

            return opt.isPresent();
        } else {
            TextContents content = textContentsRepository.getReferenceById(contentId);
            Optional<ContentsLibrary> opt = contentsLibraryRepository.findByTextContentsAndUsers_Id(content, userId);

            return opt.isPresent();
        }
    }

    private boolean checkLevel(String contentType, Long contentId, float start, float end) {
        if (contentType.equalsIgnoreCase("video")) {
            Optional<VideoContents> contentOpt = videoContentsRepository.findById(contentId);
            assert contentOpt.isPresent();

            float textGrade = contentOpt.get().getTextGrade();
            return textGrade >= start && textGrade <= end;
        } else {
            Optional<TextContents> contentOpt = textContentsRepository.findById(contentId);
            assert contentOpt.isPresent();

            float textGrade = contentOpt.get().getTextGrade();
            return textGrade >= start && textGrade <= end;
        }
    }
}
