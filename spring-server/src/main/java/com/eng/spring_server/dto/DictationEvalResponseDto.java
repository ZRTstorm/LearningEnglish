package com.eng.spring_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DictationEvalResponseDto {
    private double overallScore;       // 전체 정확도 점수
    private List<Double> scores;       // 세부 항목 점수
}
