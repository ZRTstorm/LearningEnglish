package com.eng.spring_server.controller;

import com.eng.spring_server.service.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
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
}
