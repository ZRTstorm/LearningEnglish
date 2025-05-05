package com.eng.spring_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class WordResponse {
    private String word;
    private String phonetic;
    private String audioUrl;
    private List<DefinitionResponse> definitions;
}
