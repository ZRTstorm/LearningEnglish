package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class SentenceListResponseDto {
    private Long id;
    private String text;
}
