package com.eng.spring_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ContentIdDto {

    private String contentType;
    private Long contentId;
}
