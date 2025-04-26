package com.eng.spring_server.controller;

import com.eng.spring_server.domain.word.Word;
import com.eng.spring_server.service.WordService;
import com.eng.spring_server.dto.WordRequest;
import com.eng.spring_server.dto.WordResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/words")
public class WordController {

    private final WordService wordService;

    @Operation(summary = "단어 저장", description = "단어를 입력하면 뜻을 검색해서 저장합니다.")
    @PostMapping
    public ResponseEntity<WordResponse> saveWord(@RequestBody WordRequest request) {
        Word word = wordService.saveWord(request.getWord());
        return ResponseEntity.ok(new WordResponse(word));
    }

    @Operation(summary = "단어 목록 조회", description = "저장된 모든 단어와 뜻을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WordResponse>> getAllWords() {
        List<WordResponse> response = wordService.getAllWords()
                .stream()
                .map(WordResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


}
