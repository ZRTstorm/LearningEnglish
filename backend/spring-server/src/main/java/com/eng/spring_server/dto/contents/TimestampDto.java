package com.eng.spring_server.dto.contents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimestampDto {
    private long startTimeMillis;
    private long endTimeMillis;
    private String originalText;
    private String translatedText;
}
