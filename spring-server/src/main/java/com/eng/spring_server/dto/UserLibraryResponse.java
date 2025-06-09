package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserLibraryResponse {
    // Id
    private Long libraryId;

    // Content Id
    private String contentType;
    private Long contentId;

    // User Content Title
    private String userTitle;
    private String title;
    private LocalDateTime uploadDate;

    // content Level
    private float textGrade;
    private float soundGrade;

    // Progress
    private float progress = 0f;
    private Long writeNum = 0L;
    private Double writeScore = 0d;
    private Long speechNum = 0L;
    private Double speechScore = 0d;
    private Long quizNum = 0L;
    private Double quizScore = 0d;
}
