package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class DictationResultDto {
    private Long sentenceId;
    private String userText;
    private Double grammarScore;
    private Double similarityScore;
    private String feedback;
    private LocalDateTime createdAt;
}
