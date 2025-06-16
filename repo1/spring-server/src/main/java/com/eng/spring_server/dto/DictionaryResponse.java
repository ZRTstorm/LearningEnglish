package com.eng.spring_server.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DictionaryResponse {
    private String word;
    private List<Phonetic> phonetics;
    private List<Meaning> meanings;

    @Getter
    public static class Phonetic {
        private String text;
        private String audio;
    }

    @Getter
    public static class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;

        @Getter
        public static class Definition {
            private String definition;
            private String example;
            private List<String> synonyms;
            private List<String> antonyms;
        }
    }
}
