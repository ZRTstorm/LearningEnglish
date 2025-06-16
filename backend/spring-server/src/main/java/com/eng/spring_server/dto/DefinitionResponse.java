package com.eng.spring_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DefinitionResponse {
    private String partOfSpeech;
    private String definitionEn;
    private String definitionKo;
    private String exampleEn;
    private String exampleKo;
    private List<String> synonyms;
    private List<String> antonyms;
}
