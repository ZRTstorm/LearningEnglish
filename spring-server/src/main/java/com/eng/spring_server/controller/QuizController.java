package com.eng.spring_server.controller;

import com.eng.spring_server.domain.contents.QuizData;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "순서 정렬 퀴즈 생성", description = "콘텐츠 자료에서 순서 정렬 퀴즈를 생성 한다")
    @GetMapping("/orders/{contentType}/{contentId}")
    public ResponseEntity<?> getOrdersQuiz(@PathVariable String contentType, @PathVariable Long contentId) {
        List<IndexedText> summaList = quizService.summaOrderQuiz(contentType, contentId);

        return ResponseEntity.status(HttpStatus.OK).body(summaList);
    }

    @Operation(summary = "퀴즈 데이터 저장", description = "퀴즈 데이터를 저장 한다")
    @GetMapping("/save/{quizType}/{libraryId}")
    public ResponseEntity<?> saveQuiz(@PathVariable String quizType, @PathVariable Long libraryId,
                                      @RequestParam String originalData, @RequestParam String userData, @RequestParam Long score) {
        quizService.saveQuizData(libraryId, quizType, originalData, userData, score);

        return ResponseEntity.status(HttpStatus.OK).body("saveOk");
    }

    @Operation(summary = "퀴즈 데이터 리스트 확인", description = "사용자의 퀴즈 리스트를 조회 한다")
    @GetMapping("/search/list/{libraryId}")
    public ResponseEntity<?> listQuiz(@PathVariable Long libraryId) {
        List<QuizData> quizDataList = quizService.searchListQuiz(libraryId);

        return ResponseEntity.status(HttpStatus.OK).body(quizDataList);
    }

    @Operation(summary = "삽입 퀴즈 피드백 제공", description = "사용자가 저장한 퀴즈 피드백을 제공 한다")
    @GetMapping("/feedback/insertion/{quizId}")
    public ResponseEntity<?> feedbackInsertion(@PathVariable Long quizId) {
        InsertionFeedbackDto response = quizService.insertFeedback(quizId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "문장 배열 퀴즈 피드백 제공", description = "사용자가 저장한 배열 퀴즈 피드백을 제공 한다")
    @GetMapping("/feedback/orders/{quizId}")
    public ResponseEntity<?> feedbackOrders(@PathVariable Long quizId) {
        IndexedFeedback response = quizService.ordersFeedback(quizId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
