package com.eng.spring_server.dto.Pronunciation;

public class PronunciationEvalResponseDto {
    private double accuracy;
    private double fluency;
    private double completeness;
    private double pronunciation;

    public PronunciationEvalResponseDto(double accuracy, double fluency, double completeness, double pronunciation) {
        this.accuracy = accuracy;
        this.fluency = fluency;
        this.completeness = completeness;
        this.pronunciation = pronunciation;
    }

    public double getAccuracy() { return accuracy; }
    public double getFluency() { return fluency; }
    public double getCompleteness() { return completeness; }
    public double getPronunciation() { return pronunciation; }
}