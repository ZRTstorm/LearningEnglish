package com.eng.spring_server.dto.Pronunciation;

import com.eng.spring_server.dto.dictation.TtsSentenceItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PronunciationStartResponseDto {
    private String sentence;
    private Long sentenceId;
    private List<TtsSentenceItemDto> ttsContents;
    private float level;
    private Long contentLibraryId;
}
