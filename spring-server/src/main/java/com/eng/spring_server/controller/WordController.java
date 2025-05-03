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

    @Operation(summary = "단어 저장", description = "단어와 사용자 uid를 받아 사용자 단어장에 저장")
    @PostMapping("/add")
    public ResponseEntity<Void> addWordToUser(@RequestParam String word, @RequestParam String uid) {
        wordService.saveWordForUser(word, uid);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 단어 목록 조회", description = "사용자의 uid로 저장된 단어 전체를 조회")
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<Word>> getWordsByUser(@PathVariable String uid) {
        return ResponseEntity.ok(wordService.getWordsByUser(uid));
    }

    @Operation(summary = "단어 상세 조회", description = "단어 ID로 단어 정보와 정의 목록을 조회")
    @GetMapping("/{wordId}")
    public ResponseEntity<Word> getWordDetail(@PathVariable Long wordId) {
        return ResponseEntity.ok(wordService.getWordDetail(wordId));
    }

    @Operation(summary = "단어 삭제", description = "사용자 uid와 단어 ID를 기반으로 단어를 단어장에서 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteWordForUser(@RequestParam String uid, @RequestParam Long wordId) {
        wordService.deleteWordForUser(uid, wordId);
        return ResponseEntity.ok().build();
    }


}
