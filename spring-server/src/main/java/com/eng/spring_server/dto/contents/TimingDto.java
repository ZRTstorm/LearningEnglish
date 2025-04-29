package com.eng.spring_server.dto.contents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimingDto {
    private long startTimeMillis;
    private long endTimeMillis;
    private String originalText;
    private String translatedText;
}
