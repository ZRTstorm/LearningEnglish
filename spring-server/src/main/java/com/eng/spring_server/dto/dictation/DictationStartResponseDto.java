package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictationStartResponseDto {
    private String text;
    private Long sentenceId;
    private List<TtsSentenceItemDto> contents;
}
