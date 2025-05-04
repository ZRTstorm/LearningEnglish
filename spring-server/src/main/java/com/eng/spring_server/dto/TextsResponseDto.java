package com.eng.spring_server.dto;

import com.eng.spring_server.dto.contents.TimestampDto;
import com.eng.spring_server.dto.contents.WordDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class TextsResponseDto {

    private String contentType;
    private Long contentId;

    private String title;
    private float textGrade;

    private String originalText;
    private String translatedText;

    private List<TextFile> textFiles;
    private List<WordDto> words;

    @Getter @Setter
    public static class TextFile {
        private String filePath;
        private List<TimestampDto> sentences;
    }
}
