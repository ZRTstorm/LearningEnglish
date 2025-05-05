package com.eng.spring_server.dto.contents;

import com.eng.spring_server.dto.TextTimeDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BasicResponse {

    private String file_path;
    private List<TextTimeDto> text;
}
