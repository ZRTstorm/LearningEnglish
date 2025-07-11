package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TtsSentenceItemDto {
    private String text;
    private String filePathUs;
    private String filePathGb;
    private String filePathAu;
}
