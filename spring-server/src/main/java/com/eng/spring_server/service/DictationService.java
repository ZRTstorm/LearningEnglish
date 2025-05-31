package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.dictation.DictationList;
import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.dto.dictation.*;
import com.eng.spring_server.repository.ContentsLibraryRepository;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.SummarizationRepository;
import com.eng.spring_server.repository.UsersRepository;
import com.eng.spring_server.repository.dictation.DictationListRepository;
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
    private final DictationListRepository dictationListRepository;
    private final UsersRepository usersRepository;
    private final ContentsLibraryRepository contentsLibraryRepository;

    public DictationEvalResponseDto evaluateDictation(DictationEvalRequestDto dto) {
        String reference = sentenceRepository.findById(dto.getSentenceId())
                .orElseThrow(() -> new RuntimeException("문장을 찾을 수 없습니다."))
                .getText();

        String userInput = dto.getUserText();
        int editDistance = calculateEditDistance(reference, userInput);
        double accuracyScore = calculateAccuracy(reference, userInput);
        double similarityScore = 1.0 - ((double) editDistance / reference.length());
        double grammarScore = 1.0;

        List<String> incorrectWords = new ArrayList<>();
        List<String> feedbackMessages = new ArrayList<>();

        try {
            JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
            List<RuleMatch> matches = langTool.check(userInput);
            grammarScore = 1.0 - ((double) matches.size() / Math.max(1, userInput.split(" ").length));

            for (RuleMatch match : matches) {
                String ruleId = match.getRule().getId();
                String feedback;

                switch (ruleId) {
                    case "MORFOLOGIK_RULE_EN_US":
                    case "DID_YOU_MEAN":
                        String wrongWord = userInput.substring(match.getFromPos(), match.getToPos());
                        String suggestion = match.getSuggestedReplacements().isEmpty() ? "수정안" : match.getSuggestedReplacements().get(0);
                        feedback = "'" + wrongWord + "'는 '" + suggestion + "'의 오타일 수 있어요.";
                        break;
                    default:
                        String rawMessage = match.getMessage();
                        String cleanedMessage = cleanSuggestionTags(rawMessage);
                        feedback = koreanFeedback(ruleId);
                        if (feedback == null) {
                            feedback = "문법 오류가 있습니다: " + cleanedMessage;
                        }
                        break;
                }

                feedbackMessages.add(feedback);
                if (!match.getSuggestedReplacements().isEmpty()) {
                    incorrectWords.add(match.getSuggestedReplacements().get(0));
                }
            }
        } catch (IOException e) {
            feedbackMessages.add("LanguageTool 분석 중 오류가 발생했습니다.");
        }

        // UID → UserId로 수정
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ContentsLibrary contentsLibrary;
        if ("video".equals(dto.getContentType())) {
            contentsLibrary = contentsLibraryRepository
                    .findByUsersAndContentsTypeAndVideoContents_Id(user, "video", dto.getContentId())
                    .orElseThrow(() -> new RuntimeException("콘텐츠 라이브러리를 찾을 수 없습니다."));
        } else if ("text".equals(dto.getContentType())) {
            contentsLibrary = contentsLibraryRepository
                    .findByUsersAndContentsTypeAndTextContents_Id(user, "text", dto.getContentId())
                    .orElseThrow(() -> new RuntimeException("콘텐츠 라이브러리를 찾을 수 없습니다."));
        } else {
            throw new RuntimeException("잘못된 콘텐츠 타입입니다.");
        }

        Sentence sentence = sentenceRepository.findById(dto.getSentenceId())
                .orElseThrow(() -> new RuntimeException("문장을 찾을 수 없습니다."));
        float level = sentence.getSentenceLevel() != null ? sentence.getSentenceLevel().getSpeechGrade() * 100f : -1f;

        DictationList dictation = new DictationList();
        dictation.setSentenceId(dto.getSentenceId());
        dictation.setContentsLibrary(contentsLibrary);
        dictation.setSentenceLevel(level);
        dictation.setUserText(userInput);
        dictation.setScore(accuracyScore);
        dictation.setSimilarityScore(similarityScore);
        dictation.setGrammarScore(grammarScore);
        dictation.setFeedback(String.join(" ", feedbackMessages));
        dictationListRepository.save(dictation);

        return new DictationEvalResponseDto(reference, userInput, accuracyScore, editDistance, incorrectWords, feedbackMessages);
    }


    // HTML <suggestion> 태그 제거 함수
    private String cleanSuggestionTags(String input) {
        return input.replaceAll("</?suggestion>", "");
    }

    // 피드백 한글 매핑 함수
    private String koreanFeedback(String ruleId) {
        switch (ruleId) {
            case "EN_A_VS_AN": return "a/an 사용에 문제가 있습니다. 모음/자음 여부에 따라 적절히 수정하세요.";
            case "EN_COMMA_SPACE": return "쉼표 뒤에는 공백이 필요합니다.";
            case "EN_QUOTES": return "따옴표 사용에 주의하세요.";
            case "EN_UNPAIRED_BRACKETS": return "괄호의 짝이 맞지 않습니다.";
            case "UPPERCASE_SENTENCE_START": return "문장은 대문자로 시작해야 합니다.";
            case "DASH_RULE": return "대시(-) 사용 방법이 잘못되었습니다.";
            case "DOUBLE_PUNCTUATION": return "구두점이 중복되었습니다.";
            case "WHITESPACE_RULE": return "공백 사용이 부적절합니다.";
            case "WORD_REPEAT_RULE": return "단어가 반복되었습니다.";
            case "SENTENCE_WHITESPACE": return "문장 끝에 불필요한 공백이 있습니다.";
            case "ENGLISH_WORD_REPEAT_BEGINNING_RULE": return "문장 시작 부분에 단어 반복이 있습니다.";
            case "POSSESSIVE_APOSTROPHE": return "소유격 사용에 문제가 있습니다. apostrophe(')를 확인하세요.";
            case "THEN_NOT_THAN": return "'than' 대신 'then'을 사용한 것 같아요.";
            case "ITS_VS_ITS": return "'it's'와 'its' 사용을 혼동했을 수 있습니다.";
            case "THERE_VS_THEIR": return "'there'와 'their'을 구분해 주세요.";
            case "AFFECT_EFFECT": return "'affect'와 'effect'의 차이를 구분해 주세요.";
            case "I_E_E_G": return "'i.e.' 또는 'e.g.' 사용 시 올바른 문법을 따르세요.";
            case "TO_TOO": return "'to'와 'too'의 의미를 구분해서 사용하세요.";
            default: return null;
        }
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

        float level = selected.getSentenceLevel() != null
                ? selected.getSentenceLevel().getSpeechGrade() * 100f
                : -1f;

        // UID → UserId로 수정
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
                    new TtsSentenceItemDto(text,
                            generated.getFilePathUs(),
                            generated.getFilePathGb(),
                            generated.getFilePathAu())
            );
        }

        return new DictationStartResponseDto(text, selected.getId(), contents, level, contentsLibrary.getId());
    }




}
