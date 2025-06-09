package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ContentsSummaDto {

    private String contentType;
    private Long contentId;

    private float textGrade;
    private float soundGrade;

    private String videoUrl;
    private String title;
}
