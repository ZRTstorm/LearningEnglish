package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AllContentsResponse {
    private String file_path;
    private float text_grade;
    private float sound_grade;
    private List<TextTimeDto> text;
    private List<String> translated;
}
