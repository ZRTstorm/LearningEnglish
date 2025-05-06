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
    private String title;
    private LocalDateTime uploadDate;

    // content Level
    private float textGrade;
    private float soundGrade;
}
