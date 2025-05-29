package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.dto.dictation.*;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.SummarizationRepository;
import com.eng.spring_server.repository.dictation.TtsSentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictationService {

    private final SentenceRepository sentenceRepository;
    private final SummarizationRepository summarizationRepository;
    private final PythonApiClient pythonApiClient;
    private final TextOperationService textOperationService;
    private final TtsSentenceRepository ttsSentenceRepository;
    private final TtsService ttsService;

    public DictationEvalResponseDto evaluateDictation(DictationEvalRequestDto dto) {
        String reference;
        if ("summary".equalsIgnoreCase(dto.getSentenceType())) {
            reference = summarizationRepository.findById(dto.getSentenceId())
                    .orElseThrow(() -> new RuntimeException("요약 문장을 찾을 수 없습니다."))
                    .getText();
        } else {
            reference = sentenceRepository.findById(dto.getSentenceId())
                    .orElseThrow(() -> new RuntimeException("중요 문장을 찾을 수 없습니다."))
                    .getText();
        }

        String userInput = dto.getUserText();
        int editDistance = calculateEditDistance(reference, userInput);
        double accuracyScore = calculateAccuracy(reference, userInput);

        List<String> incorrectWords = new ArrayList<>();
        List<String> feedbackMessages = new ArrayList<>();
        try {
            JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
            List<RuleMatch> matches = langTool.check(userInput);

            for (RuleMatch match : matches) {
                feedbackMessages.add(match.getMessage());
                if (!match.getSuggestedReplacements().isEmpty()) {
                    incorrectWords.add(match.getSuggestedReplacements().get(0));
                }
            }
        } catch (IOException e) {
            feedbackMessages.add("LanguageTool 분석 중 오류가 발생했습니다.");
        }

        return new DictationEvalResponseDto(reference, userInput, accuracyScore, editDistance, incorrectWords, feedbackMessages);
    }

    private int calculateEditDistance(String ref, String user) {
        int[][] dp = new int[ref.length() + 1][user.length() + 1];
        for (int i = 0; i <= ref.length(); i++) {
            for (int j = 0; j <= user.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else if (ref.charAt(i - 1) == user.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j],
                            Math.min(dp[i][j - 1], dp[i - 1][j - 1])
                    );
                }
            }
        }
        return dp[ref.length()][user.length()];
    }

    private double calculateAccuracy(String ref, String user) {
        int editDist = calculateEditDistance(ref, user);
        int maxLen = Math.max(ref.length(), user.length());
        return maxLen == 0 ? 100.0 : (1 - ((double) editDist / maxLen)) * 100;
    }

    public DictationStartResponseDto getRandomDictationSentence(DictationStartRequestDto dto) {
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

        String text = selected.getText();
        SentenceType sentenceType = SentenceType.IMPORTANT;

        Optional<TtsSentence> existing = ttsSentenceRepository.findBySentenceIdAndSentenceType(selected.getId(), sentenceType);

        float level = selected.getSentenceLevel() != null ? selected.getSentenceLevel().getSpeechGrade() * 100f : -1f;

        if (existing.isPresent()) {
            TtsSentence tts = existing.get();
            List<TtsSentenceItemDto> contents = List.of(
                    new TtsSentenceItemDto(text, tts.getFilePathUs(), tts.getFilePathGb(), tts.getFilePathAu())
            );
            return new DictationStartResponseDto(text, selected.getId(), contents, level);
        }

        TtsSentence generated = ttsService.generateTtsFiles(selected.getId(), sentenceType, text);
        List<TtsSentenceItemDto> contents = List.of(
                new TtsSentenceItemDto(text,
                        "http://54.252.44.80:8080/" + generated.getFilePathUs(),
                        "http://54.252.44.80:8080/" + generated.getFilePathGb(),
                        "http://54.252.44.80:8080/" + generated.getFilePathAu())
        );

        return new DictationStartResponseDto(text, selected.getId(), contents, level);
    }
}
