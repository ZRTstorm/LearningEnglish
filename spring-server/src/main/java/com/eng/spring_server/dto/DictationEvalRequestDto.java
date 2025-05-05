package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DictationEvalRequestDto {
    private String sentenceType;  // "summary" or "important"
    private Long sentenceId;      // 평가 대상 문장 ID
    private String userText;      // 사용자가 입력한 문장
}
