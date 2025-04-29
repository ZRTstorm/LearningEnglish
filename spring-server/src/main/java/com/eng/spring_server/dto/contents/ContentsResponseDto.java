package com.eng.spring_server.dto.contents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentsResponseDto {
    private String contentType; // "TEXT" 또는 "VIDEO"
    private String contentId;   // ex) vid001
    private String originalText;
    private String translatedText;
    private List<MappingDto> mapping;    // 문장 매핑 리스트
    private List<TimingDto> timings;     // 영상 전용 타임스탬프 리스트
    private List<WordDto> words;          // 단어 리스트
}
