package com.eng.spring_server.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.repository.dictation.TtsSentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class TtsService {

    private final TtsSentenceRepository ttsSentenceRepository;

    public TtsSentence generateTtsFiles(Long sentenceId, SentenceType sentenceType, String text) {
        String baseFileName = "sentence-" + sentenceId;
        String usPath = synthesize(text, baseFileName + "_us", "en-US");
        String gbPath = synthesize(text, baseFileName + "_gb", "en-GB");
        String auPath = synthesize(text, baseFileName + "_au", "en-AU");

        return ttsSentenceRepository.save(TtsSentence.builder()
                .sentenceId(sentenceId)
                .sentenceType(sentenceType)
                .filePathUs(usPath)
                .filePathGb(gbPath)
                .filePathAu(auPath)
                .build());
    }

    @Value("${gcp.tts.credentials.path}")
    private String ttsCredentialsPath;

    private String synthesize(String text, String fileName, String languageCode) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ClassPathResource(ttsCredentialsPath).getInputStream()
            );
            TextToSpeechSettings settings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build();

            try (TextToSpeechClient client = TextToSpeechClient.create(settings)) {
                SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

                VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                        .setLanguageCode(languageCode)
                        .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                        .build();

                AudioConfig audioConfig = AudioConfig.newBuilder()
                        .setAudioEncoding(AudioEncoding.MP3)
                        .build();

                SynthesizeSpeechResponse response = client.synthesizeSpeech(input, voice, audioConfig);
                ByteString audioContents = response.getAudioContent();

                String path = "downloads/" + fileName + ".mp3";
                try (OutputStream out = new FileOutputStream(path)) {
                    out.write(audioContents.toByteArray());
                }
                return path;
            }
        } catch (Exception e) {
            throw new RuntimeException("TTS 생성 실패 (" + languageCode + "): " + e.getMessage(), e);
        }
    }

}
