package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AllContents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;
    private float textGrade;
    private float soundGrade;

    @Column(unique = true) // 유튜브 영상 중복 방지
    private String videoKey;

    private String title;          // 영상 제목
    private int difficultyLevel;   // 난이도
    private String category;       // 카테고리
    private LocalDateTime uploadedAt;

    @Column(columnDefinition = "TEXT")
    private String translatedText;

    @OneToMany(mappedBy = "allContents", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TextTime> textTimes = new ArrayList<>();
}
