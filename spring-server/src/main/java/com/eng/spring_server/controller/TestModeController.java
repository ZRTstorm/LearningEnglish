package com.eng.spring_server.controller;

import com.eng.spring_server.dto.ContentIdDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationEvalResponseDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationStartResponseDto;
import com.eng.spring_server.dto.dictation.DictationEvalRequestDto;
import com.eng.spring_server.dto.dictation.DictationEvalResponseDto;
import com.eng.spring_server.dto.dictation.DictationStartResponseDto;
import com.eng.spring_server.service.DictationService;
import com.eng.spring_server.service.PronunciationService;
import com.eng.spring_server.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/testMode")
public class TestModeController {

    private final DictationService dictationService;
    private final PronunciationService pronunciationService;
    private final QuizService quizService;

    @Operation(summary = "테스트 모드 자료 요청", description = "테스트 모드 문제에 필요한 자료를 요청 한다")
    @GetMapping("/{contentType}/{contentId}/{testOrder}")
    public ResponseEntity<?> getTestModeItem(@PathVariable Long testOrder,
                                             @PathVariable String contentType, @PathVariable Long contentId) {
        Object response = null;
        if (testOrder == 1 || testOrder == 2) {
            response = dictationService.getTestDictation(testOrder, contentType, contentId);
        } else if (testOrder == 3 || testOrder == 4) {
            response = pronunciationService.getTestPronoun(testOrder, contentType, contentId);
        } else if (testOrder == 5) {
            response = quizService.sentenceInsertionQuiz(new ContentIdDto(contentType, contentId));
        } else {
            response = quizService.summaOrderQuiz(contentType, contentId);
        }

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "테스트 모드 받아쓰기 채점", description = "테스트 모드에서 받아 쓰기 피드백을 요청 한다")
    @PostMapping("/write/eval")
    public ResponseEntity<?> evalTestDictation(@RequestBody DictationEvalRequestDto dto) {
        DictationEvalResponseDto response = dictationService.evalTestDictation(dto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
