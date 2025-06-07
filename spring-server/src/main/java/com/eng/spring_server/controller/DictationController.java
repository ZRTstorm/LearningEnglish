package com.eng.spring_server.controller;

import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.dto.dictation.*;
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

    @Operation(summary = "받아쓰기 시작", description = "콘텐츠 ID와 문장 종류(summary/important)에 따라 TTS가 포함된 문장을 반환합니다.")
    @PostMapping("/start")
    public ResponseEntity<DictationStartResponseDto> startDictation(@RequestBody DictationStartRequestDto dto) {
        DictationStartResponseDto response = dictationService.getRandomDictationSentence(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "받아쓰기 채점", description = "사용자의 입력 문장을 기준으로 정확도를 평가하고 점수와 정답을 반환합니다.")
    @PostMapping("/eval")
    public ResponseEntity<DictationEvalResponseDto> evaluateDictation(@RequestBody DictationEvalRequestDto dto) {
        DictationEvalResponseDto response = dictationService.evaluateDictation(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "받아쓰기 조회")
    @GetMapping("/list/{libraryId}")
    public List<DictationResultDto> getBestDictationResults(@PathVariable Long libraryId) {
        return dictationService.getBestResultsByLibraryId(libraryId);
    }
}
