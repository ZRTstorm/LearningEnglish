package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictationStartRequestDto {
    private String uid;
    private Long contentId;
    private String contentType;
    // private String sentenceType;  // "summary" 또는 "important"
    private int sentenceLevel;    // 프론트에서 보내주는 난이도 (0~100)

}
