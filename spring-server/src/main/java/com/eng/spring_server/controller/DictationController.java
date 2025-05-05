package com.eng.spring_server.controller;

import com.eng.spring_server.dto.*;
import com.eng.spring_server.service.DictationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dictation")
@RequiredArgsConstructor
public class DictationController {

    private final DictationService dictationService;

    @PostMapping("/summary/audio")
    public ResponseEntity<String> summaryTts(@RequestBody DictationRequestDto dto) {
        return ResponseEntity.ok(dictationService.generateTtsAudio(dto.getSentenceId(), "summary"));
    }

    @PostMapping("/important/audio")
    public ResponseEntity<String> importantTts(@RequestBody DictationRequestDto dto) {
        return ResponseEntity.ok(dictationService.generateTtsAudio(dto.getSentenceId(), "important"));
    }


    @PostMapping("/summary/evaluate")
    public ResponseEntity<DictationEvalResponseDto> summaryEval(@RequestBody DictationEvalRequestDto dto) {
        dto.setSentenceType("summary");
        return ResponseEntity.ok(dictationService.evaluateDictation(dto));
    }

    @PostMapping("/important/evaluate")
    public ResponseEntity<DictationEvalResponseDto> importantEval(@RequestBody DictationEvalRequestDto dto) {
        dto.setSentenceType("important");
        return ResponseEntity.ok(dictationService.evaluateDictation(dto));
    }

    @PostMapping("/summary/sentences")
    public ResponseEntity<List<SentenceListResponseDto>> summarySentences(@RequestBody SentenceListRequestDto dto) {
        return ResponseEntity.ok(dictationService.getSummarySentence(dto));
    }

    @PostMapping("/important/sentences")
    public ResponseEntity<List<SentenceListResponseDto>> importantSentences(@RequestBody SentenceListRequestDto dto) {
        return ResponseEntity.ok(dictationService.getImportantSentences(dto));
    }
}
