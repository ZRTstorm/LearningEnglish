package com.eng.spring_server.domain.word;

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
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @Column(unique = true)
    private String word; // 단어 자체

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Definition> definitions = new ArrayList<>(); // 단어에 대한 정의 목록

    @OneToMany(mappedBy = "word")
    private List<UserWord> userWords = new ArrayList<>(); // 이 단어를 가진 사용자들과의 관계
}