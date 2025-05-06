package com.eng.spring_server.controller;

import com.eng.spring_server.dto.*;
import com.eng.spring_server.service.DictationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dictation")
@RequiredArgsConstructor
public class DictationController {

    private final DictationService dictationService;

    @Operation(summary = "요약 문장에 대한 mp3 경로 반환", description = "해당하는 요약 문장에 대해서 미국/영국/호주 발음 mp3파일 경로를 제공")
    @PostMapping("/summary/audio")
    public ResponseEntity<String> summaryTts(@RequestBody DictationRequestDto dto) {
        return ResponseEntity.ok(dictationService.generateTtsAudio(dto.getSentenceId(), "summary"));
    }

    @Operation(summary = "중요 문장에 대한 mp3 경로 반환", description = "해당하는 중요 문장에 대해서 미국/영국/호주 발음 mp3파일 경로를 제공")
    @PostMapping("/important/audio")
    public ResponseEntity<String> importantTts(@RequestBody DictationRequestDto dto) {
        return ResponseEntity.ok(dictationService.generateTtsAudio(dto.getSentenceId(), "important"));
    }

    @Operation(summary = "요약 문장에 대한 받아쓰기 답안 평가", description = "사용자가 쓴 답안에 대해 평가를 반환합니다.")
    @PostMapping("/summary/evaluate")
    public ResponseEntity<DictationEvalResponseDto> summaryEval(@RequestBody DictationEvalRequestDto dto) {
        dto.setSentenceType("summary");
        return ResponseEntity.ok(dictationService.evaluateDictation(dto));
    }

    @Operation(summary = "주요 문장에 대한 받아쓰기 답안 평가", description = "사용자가 쓴 답안에 대해 평가를 반환합니다.")
    @PostMapping("/important/evaluate")
    public ResponseEntity<DictationEvalResponseDto> importantEval(@RequestBody DictationEvalRequestDto dto) {
        dto.setSentenceType("important");
        return ResponseEntity.ok(dictationService.evaluateDictation(dto));
    }

    @Operation(summary = "요약 문장의 리스트를 반환", description = "받아쓰기 할 문장을 고르기위한 문장 ID를 파악할 수 있습니다.")
    @PostMapping("/summary/sentences")
    public ResponseEntity<List<SentenceListResponseDto>> summarySentences(@RequestBody SentenceListRequestDto dto) {
        return ResponseEntity.ok(dictationService.getSummarySentence(dto));
    }

    @Operation(summary = "중요 문장의 리스트를 반환", description = "받아쓰기 할 문장을 고르기위한 문장 ID를 파악할 수 있습니다.")
    @PostMapping("/important/sentences")
    public ResponseEntity<List<SentenceListResponseDto>> importantSentences(@RequestBody SentenceListRequestDto dto) {
        return ResponseEntity.ok(dictationService.getImportantSentences(dto));
    }
}
