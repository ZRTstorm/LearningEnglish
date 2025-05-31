package com.eng.spring_server.controller;

import com.eng.spring_server.dto.Pronunciation.PronunciationEvalRequestDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationEvalResponseDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationStartRequestDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationStartResponseDto;
import com.eng.spring_server.service.PronunciationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pronunciation")
@RequiredArgsConstructor
public class PronunciationController {

    private final PronunciationService pronunciationService;

    @PostMapping("/start")
    public ResponseEntity<PronunciationStartResponseDto> startPronunciation(@RequestBody PronunciationStartRequestDto request) {
        return ResponseEntity.ok(pronunciationService.getStartSentence(request));
    }

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






}
