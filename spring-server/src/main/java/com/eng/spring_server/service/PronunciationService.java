package com.eng.spring_server.service;

import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.domain.pronunciation.PronunciationList;
import com.eng.spring_server.dto.Pronunciation.PronunciationEvalResponseDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationStartRequestDto;
import com.eng.spring_server.dto.Pronunciation.PronunciationStartResponseDto;
import com.eng.spring_server.dto.dictation.TtsSentenceItemDto;
import com.eng.spring_server.repository.ContentsLibraryRepository;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.UsersRepository;
import com.eng.spring_server.repository.dictation.TtsSentenceRepository;
import com.eng.spring_server.repository.pronunciation.PronunciationListRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;
import org.apache.commons.io.FilenameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PronunciationService {

    @Value("${azure.speech.key}")
    private String azureSpeechKey;

    @Value("${azure.speech.region}")
    private String azureSpeechRegion;

    private final SentenceRepository sentenceRepository;
    private final UsersRepository usersRepository;
    private final ContentsLibraryRepository contentsLibraryRepository;
    private final TtsSentenceRepository ttsSentenceRepository;
    private final TtsService ttsService;
    private final PronunciationListRepository pronunciationListRepository;

    public PronunciationStartResponseDto getStartSentence(PronunciationStartRequestDto dto) {
        List<Sentence> candidates = sentenceRepository
                .findByContentAndTypeOrderByLastAccessed(dto.getContentId(), dto.getContentType());

        if (candidates.isEmpty()) {
            throw new RuntimeException("해당 콘텐츠에 문장이 존재하지 않습니다.");
        }

        double normalizedTarget = dto.getSentenceLevel() / 100.0;

        List<Sentence> nearest = candidates.stream()
                .filter(s -> s.getSentenceLevel() != null)
                .filter(s -> s.getSentenceLevel().getSpeechGrade() >= 0.0f)
                .sorted(Comparator.comparingDouble(s ->
                        Math.abs(s.getSentenceLevel().getSpeechGrade() - normalizedTarget)))
                .limit(4)
                .collect(Collectors.toList());

        if (nearest.isEmpty()) {
            throw new RuntimeException("조건에 맞는 문장을 찾을 수 없습니다.");
        }

        Sentence selected = nearest.get(new Random().nextInt(nearest.size()));
        selected.setLastAccessedAt(LocalDateTime.now());
        sentenceRepository.save(selected);

        float level = selected.getSentenceLevel() != null
                ? selected.getSentenceLevel().getSpeechGrade() * 100f
                : -1f;

        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        ContentsLibrary contentsLibrary = contentsLibraryRepository
                .findByUserAndContentTypeAndContentId(user, dto.getContentType(), dto.getContentId())
                .orElseThrow(() -> new RuntimeException("콘텐츠 라이브러리를 찾을 수 없습니다."));

        String text = selected.getText();
        SentenceType sentenceType = SentenceType.IMPORTANT;
        Optional<TtsSentence> existingTts = ttsSentenceRepository.findBySentenceIdAndSentenceType(selected.getId(), sentenceType);

        List<TtsSentenceItemDto> contents;
        if (existingTts.isPresent()) {
            TtsSentence tts = existingTts.get();
            contents = List.of(
                    new TtsSentenceItemDto(text, tts.getFilePathUs(), tts.getFilePathGb(), tts.getFilePathAu())
            );
        } else {
            TtsSentence generated = ttsService.generateTtsFiles(selected.getId(), sentenceType, text);
            contents = List.of(
                    new TtsSentenceItemDto(text, generated.getFilePathUs(), generated.getFilePathGb(), generated.getFilePathAu())
            );
        }

        return new PronunciationStartResponseDto(text, selected.getId(), contents, level, contentsLibrary.getId());
    }




    public PronunciationEvalResponseDto evaluatePronunciation(
            MultipartFile audioFile, String referenceText, Long sentenceId, Long contentsLibraryId) throws Exception {

        String ext = FilenameUtils.getExtension(audioFile.getOriginalFilename()).toLowerCase();
        File inputFile = File.createTempFile("input", "." + ext);
        audioFile.transferTo(inputFile);

        File tempFile;
        if ("mp3".equals(ext)) {
            tempFile = File.createTempFile("converted", ".wav");
            convertMp3ToWavWithFfmpeg(inputFile, tempFile);
        } else if ("wav".equals(ext)) {
            tempFile = inputFile;
        } else {
            throw new IllegalArgumentException("Only .mp3 and .wav files are supported");
        }

        SpeechConfig config = SpeechConfig.fromSubscription(azureSpeechKey, azureSpeechRegion);
        config.setSpeechRecognitionLanguage("en-US");

        PronunciationAssessmentConfig pronunciationConfig =
                new PronunciationAssessmentConfig(referenceText,
                        PronunciationAssessmentGradingSystem.HundredMark,
                        PronunciationAssessmentGranularity.Phoneme,
                        true);
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(tempFile.getAbsolutePath());
        SpeechRecognizer recognizer = new SpeechRecognizer(config, audioConfig);
        pronunciationConfig.applyTo(recognizer);

        SpeechRecognitionResult result = recognizer.recognizeOnceAsync().get();
        PronunciationAssessmentResult assessment = PronunciationAssessmentResult.fromResult(result);

        String json = result.getProperties().getProperty(PropertyId.SpeechServiceResponse_JsonResult);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(json);

        JsonNode worstWord = null;
        double minScore = Double.MAX_VALUE;
        for (JsonNode word : root.path("NBest").get(0).path("Words")) {
            double score = word.path("PronunciationAssessment").path("AccuracyScore").asDouble();
            if (score < minScore) {
                minScore = score;
                worstWord = word;
            }
        }

        String feedbackMessage = null;

        if (worstWord != null && minScore < 95) {
            String wordText = worstWord.path("Word").asText();
            JsonNode phonemes = worstWord.path("Phonemes");
            JsonNode worstPhoneme = null;
            double minPhonemeScore = Double.MAX_VALUE;

            StringBuilder ipaBuilder = new StringBuilder();
            for (JsonNode phoneme : phonemes) {
                ipaBuilder.append(phoneme.path("Phoneme").asText());
                double score = phoneme.path("PronunciationAssessment").path("AccuracyScore").asDouble();
                if (score < minPhonemeScore) {
                    minPhonemeScore = score;
                    worstPhoneme = phoneme;
                }
            }
            String ipa = ipaBuilder.toString();

            if (worstPhoneme != null) {
                String correctPhoneme = worstPhoneme.path("Phoneme").asText();
                JsonNode nBest = worstPhoneme.path("PronunciationAssessment").path("NBestPhonemes");
                String mistakenPhoneme = nBest.size() > 1 ? nBest.get(1).path("Phoneme").asText() : "정확하지 않은 발음";

                String phonemeFeedback = phonemeFeedbackMap.getOrDefault(correctPhoneme,
                        "'" + correctPhoneme + "'는 정확하게 발음되는 방법을 연습해보세요!");

                feedbackMessage = String.format(
                        "\"%s\"['%s']에서 '%s'가 '%s'에 가깝게 들려요.\n%s",
                        wordText, ipa, correctPhoneme, mistakenPhoneme, phonemeFeedback
                );
            }
        }

        double accuracy = assessment.getAccuracyScore();
        double fluency = assessment.getFluencyScore();
        double completeness = assessment.getCompletenessScore();
        double pronunciation = assessment.getPronunciationScore();

        if (sentenceId != null && contentsLibraryId != null) {
            ContentsLibrary contentsLibrary = contentsLibraryRepository.findById(contentsLibraryId)
                    .orElseThrow(() -> new RuntimeException("콘텐츠 라이브러리를 찾을 수 없습니다."));

            PronunciationList record = new PronunciationList();
            record.setSentenceId(sentenceId);
            record.setContentsLibrary(contentsLibrary);
            record.setAccuracyScore(accuracy);
            record.setFluencyScore(fluency);
            record.setCompletenessScore(completeness);
            record.setPronunciationScore(pronunciation);
            record.setFeedbackMessage(feedbackMessage);
            record.setEvaluatedAt(LocalDateTime.now());

            pronunciationListRepository.save(record);
        }

        return new PronunciationEvalResponseDto(
                accuracy, fluency, completeness, pronunciation, feedbackMessage
        );
    }


    private static final Map<String, String> phonemeFeedbackMap = Map.ofEntries(
            Map.entry("p", "'p'는 입술을 닫았다가 터뜨리듯이 소리내요!"),
            Map.entry("b", "'b'는 입술을 닫고 성대를 울리며 부드럽게 발음돼요!"),
            Map.entry("t", "'t'는 혀를 윗잇몸에 대고 빠르게 떼며 '트'처럼 소리내요!"),
            Map.entry("d", "'d'는 혀를 윗잇몸에 대고 성대를 울리며 '드'처럼 소리내요!"),
            Map.entry("k", "'k'는 혀의 뒷부분을 입천장에 붙였다가 떼면서 소리내요!"),
            Map.entry("g", "'g'는 'k'와 비슷하지만 성대를 울려요!"),
            Map.entry("f", "'f'는 윗니를 아랫입술에 살짝 대고 숨을 내쉬며 소리내요!"),
            Map.entry("v", "'v'는 'f'와 비슷하지만 성대를 울려요!"),
            Map.entry("θ", "'θ'는 혀를 윗니에 대고 '쓰'처럼 숨을 내쉬며 발음돼요!"),
            Map.entry("ð", "'ð'는 'θ'와 비슷하지만 성대를 울리며 '더'처럼 소리내요!"),
            Map.entry("s", "'s'는 혀를 윗잇몸 근처에 두고 '스'처럼 소리내요!"),
            Map.entry("z", "'z'는 's'처럼 발음하되 성대를 울려요!"),
            Map.entry("ʃ", "'ʃ'는 입술을 앞으로 모으고 '쉬'처럼 부드럽게 소리내요!"),
            Map.entry("ʒ", "'ʒ'는 'ʃ'와 비슷하지만 성대를 울려요!"),
            Map.entry("h", "'h'는 목에서 나오는 숨소리처럼 부드럽게 발음돼요!"),
            Map.entry("tʃ", "'tʃ'는 '치'처럼, 't'와 'ʃ'의 결합된 소리예요!"),
            Map.entry("dʒ", "'dʒ'는 '지'처럼, 'd'와 'ʒ'의 결합된 소리예요!"),
            Map.entry("m", "'m'는 입을 다물고 콧소리로 '음'처럼 발음돼요!"),
            Map.entry("n", "'n'는 혀를 윗잇몸에 대고 'ㄴ'처럼 콧소리로 소리내요!"),
            Map.entry("ŋ", "'ŋ'는 'ng'처럼 코 뒤에서 울리는 소리예요"),
            Map.entry("l", "'l'는 혀끝을 윗잇몸에 붙이고 부드럽게 소리내요!"),
            Map.entry("r", "'r'는 혀를 말아올리고 입을 둥글게 만들어 부드럽게 소리내요!"),
            Map.entry("j", "'j'는 '이'로 시작하는 소리처럼 발음돼요"),
            Map.entry("w", "'w'는 입술을 둥글게 오므리고 빠르게 '우'처럼 발음돼요!"),
            Map.entry("iː", "'iː'는 길게 늘이는 '이' 소리예요"),
            Map.entry("ɪ", "'ɪ'는 짧은 '이' 소리예요"),
            Map.entry("e", "'e'는 입을 살짝 벌리고 짧게 '에'처럼 발음해요"),
            Map.entry("æ", "'æ'는 입을 크게 벌리고 '애'처럼 발음해요"),
            Map.entry("ʌ", "'ʌ'는 중간 정도 입을 벌리고 '어'처럼 발음해요"),
            Map.entry("ɑː", "'ɑː'는 깊고 길게 '아'처럼 발음돼요"),
            Map.entry("ɒ", "'ɒ'는 짧은 '오'처럼 발음돼요"),
            Map.entry("ɔː", "'ɔː'는 길게 '오'처럼 발음돼요"),
            Map.entry("ʊ", "'ʊ'는 짧은 '우'처럼 발음돼요"),
            Map.entry("uː", "'uː'는 길게 '우'처럼 발음돼요"),
            Map.entry("ə", "'ə'는 힘을 주지 않고 중성적으로 발음하는 '어' 소리예요"),
            Map.entry("eɪ", "'eɪ'는 '에'에서 '이'로 미끄러지듯 나는 소리예요"),
            Map.entry("aɪ", "'aɪ'는 '아'에서 '이'로 연결되는 소리예요"),
            Map.entry("ɔɪ", "'ɔɪ'는 '오'에서 '이'로 이어지는 소리예요"),
            Map.entry("aʊ", "'aʊ'는 '아'에서 '우'로 넘어가는 소리예요"),
            Map.entry("əʊ", "'əʊ'는 '어'에서 '우'로 이어지는 소리예요"),
            Map.entry("ɪə", "'ɪə'는 '이'와 '어'가 이어지는 소리예요"),
            Map.entry("eə", "'eə'는 '에'에서 '어'로 흐르는 소리예요"),
            Map.entry("ʊə", "'ʊə'는 '우'에서 '어'로 흐르는 소리예요")
    );

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

    public String getSentenceTextById(Long sentenceId) {
        return sentenceRepository.findById(sentenceId)
                .orElseThrow(() -> new RuntimeException("문장을 찾을 수 없습니다."))
                .getText();
    }

}

