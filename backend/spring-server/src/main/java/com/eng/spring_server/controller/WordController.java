package com.eng.spring_server.controller;

import com.eng.spring_server.domain.word.Word;
import com.eng.spring_server.dto.PagedWordResponse;
import com.eng.spring_server.dto.WordResponse;
import com.eng.spring_server.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/words")
public class WordController {

    private final WordService wordService;

    @Operation(summary = "단어 저장", description = "단어와 사용자 uid를 받아 사용자 단어장에 저장")
    @PostMapping("/add")
    public ResponseEntity<Void> addWordToUser(@RequestParam String word, @RequestParam Long userId) { // userId로 변경
        wordService.saveWordForUser(word, userId); // userId로 전달
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "유저 단어 목록 조회", description = "사용자의 uid로 저장된 단어 전체를 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Word>> getWordsByUser(@PathVariable Long userId) { // userId로 변경
        return ResponseEntity.ok(wordService.getWordsByUser(userId)); // userId로 전달
    }

    @Operation(summary = "단어 문자열로 상세 조회", description = "단어 문자열을 받아 DB에 있으면 그대로 반환, 없으면 사전 API에서 가져와 저장 후 반환")
    @GetMapping("/detail")
    public ResponseEntity<WordResponse> getWordDetailByWordStr(@RequestParam String word) {
        Word wordEntity = wordService.performDictionarySearch(word); // 있으면 그대로, 없으면 저장
        WordResponse dto = wordService.convertToDto(wordEntity);
        return ResponseEntity.ok(dto);
    }


    @Operation(summary = "단어 삭제", description = "사용자 uid와 단어 ID를 기반으로 단어를 단어장에서 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteWordForUser(@RequestParam Long userId, @RequestParam Long wordId) { // userId로 변경
        wordService.deleteWordForUser(userId, wordId); // userId로 전달
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "페이지 단위 단어 조회", description = "uid와 page를 받아 유저의 단어 리스트를 10개씩 반환")
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<List<WordResponse>> getPagedWordsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page) { // userId로 변경
        List<Word> words = wordService.getPagedWordsByUser(userId, page); // userId로 전달
        List<WordResponse> response = words.stream()
                .map(wordService::convertToDto)
                .toList();

        return ResponseEntity.ok(response);
    }


}
