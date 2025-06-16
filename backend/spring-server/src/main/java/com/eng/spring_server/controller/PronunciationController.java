package com.eng.spring_server.controller;

import com.eng.spring_server.dto.Pronunciation.*;
import com.eng.spring_server.service.PronunciationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/pronunciation")
@RequiredArgsConstructor
public class PronunciationController {

    private final PronunciationService pronunciationService;

    @Operation(summary = "발음평가 시작")
    @PostMapping("/start")
    public ResponseEntity<PronunciationStartResponseDto> startPronunciation(@RequestBody PronunciationStartRequestDto request) {
        return ResponseEntity.ok(pronunciationService.getStartSentence(request));
    }

    @Operation(summary = "발음평가 채점")
    @PostMapping(value = "/evaluate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PronunciationEvalResponseDto> evaluate(
            @RequestPart("audio") MultipartFile audioFile,
            @RequestParam("sentenceId") Long sentenceId,
            @RequestParam("contentsLibraryId") Long contentsLibraryId
    ) {
        try {
            String referenceText = pronunciationService.getSentenceTextById(sentenceId);

            PronunciationEvalResponseDto result = pronunciationService.evaluatePronunciation(
                    audioFile, referenceText, sentenceId, contentsLibraryId
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "발음평가 조회")
    @GetMapping("/list/{libraryId}")
    public List<PronunciationResultDto> getBestPronunciationResults(@PathVariable Long libraryId) {
        return pronunciationService.getBestResultsByLibraryId(libraryId);
    }



}
