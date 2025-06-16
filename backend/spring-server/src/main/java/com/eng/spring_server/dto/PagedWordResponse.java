package com.eng.spring_server.dto;

import com.eng.spring_server.domain.word.Word;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PagedWordResponse {
    private List<Word> words; // 단어 목록
    private int page;         // 현재 페이지 번호
    private int size;         // 한 페이지에 담긴 단어 수
}