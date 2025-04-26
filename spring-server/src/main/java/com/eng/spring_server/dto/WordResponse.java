package com.eng.spring_server.dto;

import com.eng.spring_server.domain.word.Word;
import lombok.Getter;

@Getter
public class WordResponse {

    private Long id;
    private String word;
    private String meaning;

    public WordResponse(Word word) {
        this.id = word.getId();
        this.word = word.getWord();
        this.meaning = word.getMeaning();
    }
}
