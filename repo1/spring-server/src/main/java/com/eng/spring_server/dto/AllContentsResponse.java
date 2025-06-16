package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AllContentsResponse {
    private String file_path;
    private float text_grade;
    private float sound_grade;
    private List<TextTimeDto> text;
    private List<String> translated;

    private String title;          // 사용자에게 표시될 제목
    private String contentType;    // "TEXT" or "VIDEO"
    private int difficultyLevel;   // 난이도
    private String category;       // 카테고리

    private String contentId;    // 콘텐츠 ID (ex: vid001)
    private String uploadedAt;   // 등록된 시간
}
