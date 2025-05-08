package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictationEvalResponseDto {
    private String reference;
    private String userInput;
    private double accuracyScore;
    private int editDistance;
}
