package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TextTimeDto {
    private float start;
    private float end;
    private String text;
}
