package com.eng.spring_server.service;

import com.eng.spring_server.dto.Pronunciation.PronunciationEvalResponseDto;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.*;

@Service
public class PronunciationService {

    @Value("${azure.speech.key}")
    private String azureKey;

    @Value("${azure.speech.region}")
    private String azureRegion;

    public PronunciationEvalResponseDto evaluatePronunciation(MultipartFile audioFile, String referenceText) throws Exception {
        SpeechConfig config = SpeechConfig.fromSubscription(azureKey, azureRegion);
        config.setSpeechRecognitionLanguage("en-US");

        PronunciationAssessmentConfig pronConfig = new PronunciationAssessmentConfig(
                referenceText,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Phoneme,
                true
        );

        String ext = FilenameUtils.getExtension(audioFile.getOriginalFilename()).toLowerCase();
        File inputFile = File.createTempFile("input", "." + ext);
        audioFile.transferTo(inputFile);

        File wavFile;
        if ("mp3".equals(ext)) {
            wavFile = File.createTempFile("converted", ".wav");
            convertMp3ToWavWithFfmpeg(inputFile, wavFile);
        } else if ("wav".equals(ext)) {
            wavFile = inputFile;
        } else {
            throw new IllegalArgumentException("Only .mp3 and .wav files are supported");
        }

        try (AudioConfig audioInput = AudioConfig.fromWavFileInput(wavFile.getAbsolutePath());
             SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput)) {

            pronConfig.applyTo(recognizer);
            SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();

            if (result.getReason() != ResultReason.RecognizedSpeech) {
                throw new RuntimeException("Speech not recognized.");
            }

            PronunciationAssessmentResult assessment = PronunciationAssessmentResult.fromResult(result);

            return new PronunciationEvalResponseDto(
                    assessment.getAccuracyScore(),
                    assessment.getFluencyScore(),
                    assessment.getCompletenessScore(),
                    assessment.getPronunciationScore()
            );
        } finally {
            inputFile.delete();
            if (!inputFile.equals(wavFile)) wavFile.delete();
        }
    }

    // ffmpeg를 사용하여 mp3를 wav로 변환
    private void convertMp3ToWavWithFfmpeg(File mp3File, File wavFile) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-i", mp3File.getAbsolutePath(),
                "-ar", "16000", "-ac", "1", wavFile.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffmpeg 변환 실패. exit code = " + exitCode);
        }
    }
}