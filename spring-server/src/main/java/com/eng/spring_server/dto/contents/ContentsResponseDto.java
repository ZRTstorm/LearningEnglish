package com.eng.spring_server.dto.contents;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class ContentsResponseDto {
    // "video" or "text"
    private String contentType;
    private Long contentId;

    private String title;
    private float textGrade;
    private float soundGrade;

    private String originalText;
    private String translatedText;

    private List<TimestampDto> sentences;
    private List<WordDto> words;
}
