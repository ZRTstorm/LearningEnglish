package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TextTimeDto {
    private float start;
    private float end;
    private String text;
    private String translatedText; // 2025-04-30 번역 텍스트 추가
}
