package com.eng.spring_server.controller;

import com.eng.spring_server.dto.ContentIdDto;
import com.eng.spring_server.dto.contents.TimestampDto;
import com.eng.spring_server.service.TextOperationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/text")
public class TextSummaController {

    private final TextOperationService textOperationService;

    @Operation(summary = "중요 문장 추출", description = "콘텐츠 텍스트에서 중요 문장을 추출 한다")
    @PostMapping("/important")
    public ResponseEntity<?> extractImportant(@RequestBody ContentIdDto request) {
        // 중요 문장 추출 -> DB 저장
        textOperationService.getImportant(request);

        return ResponseEntity.status(HttpStatus.OK).body("Important Success");
    }

    @Operation(summary = "텍스트 요약 생성", description = "콘텐츠 텍스트에서 요약 텍스트를 생성 한다")
    @PostMapping("/summarization")
    public ResponseEntity<?> summarization(@RequestBody ContentIdDto request) {
        // 텍스트 요약 생성 -> DB 저장
        textOperationService.textSummarization(request);

        return ResponseEntity.status(HttpStatus.OK).body("Summarize Success");
    }

    @Operation(summary = "텍스트 중요 문장 리스트 조회", description = "콘텐츠에서 중요 문장 리스트를 조회 한다")
    @GetMapping("/important/{contentsType}/{contentId}")
    public ResponseEntity<?> listImportant(@PathVariable String contentsType, @PathVariable Long contentId) {
        List<TimestampDto> importantList = textOperationService.getImportantList(contentsType, contentId);

        return ResponseEntity.status(HttpStatus.OK).body(importantList);
    }

    @Operation(summary = "텍스트 요약문 조회", description = "콘텐츠에서 텍스트 요약문을 조회 한다")
    @GetMapping("summarization/{contentsType}/{contentId}")
    public ResponseEntity<?> getSummarizedText(@PathVariable String contentsType, @PathVariable Long contentId) {
        String summaryText = textOperationService.getSummaryText(contentsType, contentId);

        Map<String, String> response = Collections.singletonMap("summaryText", summaryText);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
