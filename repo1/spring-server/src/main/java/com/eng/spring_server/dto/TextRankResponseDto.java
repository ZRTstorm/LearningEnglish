package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class TextRankResponseDto {

    private List<TextRankResponse> rankSentences;

    @Getter @Setter
    public static class TextRankResponse {
        private int index;
        private String sentence;
    }
}
