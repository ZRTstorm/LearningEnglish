package com.eng.spring_server.domain.word;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Definition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    private String partOfSpeech; // 품사

    private String definitionEn; // 영어 정의
    private String definitionKo; // 한글 정의

    private String exampleEn; // 영어 예문
    private String exampleKo; // 예문 한글 번역

    @ElementCollection // 동의어 리스트
    private List<String> synonyms = new ArrayList<>();

    @ElementCollection // 반의어 리스트
    private List<String> antonyms = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "word_id")
    @JsonIgnore
    @JsonBackReference
    private Word word; // 소속된 단어
}
