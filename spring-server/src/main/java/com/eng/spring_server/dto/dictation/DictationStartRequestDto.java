package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictationStartRequestDto {
    private Long contentId;
    private String contentType;
    private String sentenceType;  // "summary" 또는 "important"
}
