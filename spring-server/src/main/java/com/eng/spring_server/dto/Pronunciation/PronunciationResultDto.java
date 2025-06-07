package com.eng.spring_server.dto.Pronunciation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class PronunciationResultDto {
    private Long sentenceId;
    private double accuracyScore;
    private double fluencyScore;
    private double completenessScore;
    private double pronunciationScore;
    private Map<String, Object> feedback;  // JSON 파싱된 결과
    private LocalDateTime evaluatedAt;
}
