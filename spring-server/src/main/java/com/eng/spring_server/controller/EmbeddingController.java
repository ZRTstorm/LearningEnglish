package com.eng.spring_server.controller;

import com.eng.spring_server.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/embedding")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingService embeddingService;

    @Operation(summary = "임베딩 테스트")
    @GetMapping("/openai/test")
    public Map embed(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

    @Operation(summary = "임베딩 벡터 획득", description = "콘텐츠 텍스트의 벡터 표현을 획득 한다")
    @GetMapping("/openai/{contentType}/{contentId}")
    public ResponseEntity<?> embeddingText(@PathVariable String contentType, @PathVariable Long contentId) {
        float[] embeddingVector = embeddingService.getEmbeddingVector(contentType, contentId);

        Map<String, float[]> response = Collections.singletonMap("vectorValue", embeddingVector);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "콘텐츠 벡터 저장", description = "콘텐츠의 벡터 데이터를 벡터 스토어에 저장 한다")
    @PostMapping("/vectorStore/{contentType}/{contentId}")
    public ResponseEntity<?> storeVector(@PathVariable String contentType, @PathVariable Long contentId) {
        embeddingService.saveVector(contentType, contentId);

        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @Operation(summary = "유사 콘텐츠 조회", description = "콘텐츠와 유사한 콘텐츠를 검색 한다")
    @GetMapping("search/{contentType}/{contentId}")
    public ResponseEntity<?> searchVector(@PathVariable String contentType, @PathVariable Long contentId) {
        List<Document> documents = embeddingService.searchVector(contentType, contentId);

        return ResponseEntity.status(HttpStatus.OK).body(documents);
    }
}
