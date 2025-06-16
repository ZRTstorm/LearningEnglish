package com.eng.spring_server.dto.Pronunciation;


import lombok.*;
import org.checkerframework.checker.units.qual.N;

import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PronunciationEvalResponseDto {
    private double accuracy;
    private double fluency;
    private double completeness;
    private double pronunciation;
    private List<String> feedbackMessages;
}