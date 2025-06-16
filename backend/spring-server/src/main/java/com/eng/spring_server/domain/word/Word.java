package com.eng.spring_server.domain.word;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String word;

    private String phonetic; // 발음기호
    private String audioUrl; // 발음 mp3 url

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Definition> definitions = new ArrayList<>(); // 단어에 대한 정의 목록

    @OneToMany(mappedBy = "word")
    private List<UserWord> userWords = new ArrayList<>(); // 이 단어를 가진 사용자들과의 관계
}