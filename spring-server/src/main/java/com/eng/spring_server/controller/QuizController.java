package com.eng.spring_server.controller;

import com.eng.spring_server.dto.ContentIdDto;
import com.eng.spring_server.dto.InsertionQuizDto;
import com.eng.spring_server.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "삽입 퀴즈 생성", description = "콘텐츠 자료에서 삽입 퀴즈를 생성 한다")
    @GetMapping("/insertion/{contentType}/{contentId}")
    public ResponseEntity<?> getInsertionQuiz(@PathVariable String contentType, @PathVariable Long contentId) {
        ContentIdDto request = new ContentIdDto(contentType, contentId);

        InsertionQuizDto dto = quizService.sentenceInsertionQuiz(request);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

}
