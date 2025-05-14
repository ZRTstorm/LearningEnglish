package com.eng.spring_server.service;

import com.eng.spring_server.dto.Pronunciation.PronunciationEvalResponseDto;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
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
            convertMp3ToWav(inputFile, wavFile); // 내부 변환 로직
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

    // ===========================
    // MP3 -> WAV 변환 (Java 내장 API 활용)
    // ===========================
    private void convertMp3ToWav(File mp3File, File wavFile) throws Exception {
        try (javax.sound.sampled.AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(mp3File)) {
            AudioFormat baseFormat = mp3Stream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            try (javax.sound.sampled.AudioInputStream pcmStream = AudioSystem.getAudioInputStream(decodedFormat, mp3Stream)) {
                AudioSystem.write(pcmStream, AudioFileFormat.Type.WAVE, wavFile);
            }
        }
    }
}
