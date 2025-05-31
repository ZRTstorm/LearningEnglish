package com.eng.spring_server.dto.Pronunciation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PronunciationStartRequestDto {
    private Long userId;
    private String contentType;
    private Long contentId;
    private int sentenceLevel; // 0~100
}
