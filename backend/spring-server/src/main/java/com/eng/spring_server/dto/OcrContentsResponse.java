package com.eng.spring_server.dto;

import com.eng.spring_server.dto.contents.BasicResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class OcrContentsResponse {

    private float text_grade;
    private List<BasicResponse> file_text;
    private List<String> translated;
}
