package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DictationRequestDto {
    private Long sentenceId;     // 해당 문장의 ID
}
