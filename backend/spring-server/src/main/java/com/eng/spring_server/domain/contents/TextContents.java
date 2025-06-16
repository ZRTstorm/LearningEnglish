package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class TextContents {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "text_content_id")
    private Long id;

    // Audio file 서버 경로
    private String filePath;

    // Audio 종류
    private String region;

    // contents 난이도
    private float textGrade;

    // Contents 제목
    private String title;

    // 등록 시각
    private LocalDateTime uploadDate;
}
