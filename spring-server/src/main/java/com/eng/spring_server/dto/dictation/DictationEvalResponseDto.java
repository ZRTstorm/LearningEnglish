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
    private List<String> incorrectWords; // 틀린 단어들
    private List<String> feedbackMessages; // 피드백 문장 리스트

}
