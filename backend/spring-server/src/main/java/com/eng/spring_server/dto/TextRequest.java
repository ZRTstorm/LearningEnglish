package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TextRequest {

    private String text;
    private String title;
    private Long userId;
}
