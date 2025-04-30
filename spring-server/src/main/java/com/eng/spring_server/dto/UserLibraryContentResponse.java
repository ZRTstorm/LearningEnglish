package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLibraryContentResponse {
    private String contentId;
    private String title;
    private String contentType;
    private String uploadedAt;
    private int difficultyLevel;
    private String category;
}
