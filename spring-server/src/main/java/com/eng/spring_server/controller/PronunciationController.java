package com.eng.spring_server.controller;

import com.eng.spring_server.dto.Pronunciation.PronunciationEvalResponseDto;
import com.eng.spring_server.service.PronunciationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pronunciation")
@RequiredArgsConstructor
public class PronunciationController {

    private final PronunciationService pronunciationService;

    @PostMapping("/evaluate")
    public ResponseEntity<PronunciationEvalResponseDto> evaluate(
            @RequestPart("audio") MultipartFile audioFile,
            @RequestPart("text") String referenceText
    ) {
        try {
            PronunciationEvalResponseDto result = pronunciationService.evaluatePronunciation(audioFile, referenceText);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
