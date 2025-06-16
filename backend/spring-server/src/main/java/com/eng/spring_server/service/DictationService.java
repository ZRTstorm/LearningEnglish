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
import org.springframework.transaction.annotation.Transactional;

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
        // 1) 원문 문장 가져오기
        String reference = sentenceRepository.findById(dto.getSentenceId())
                .orElseThrow(() -> new RuntimeException("문장을 찾을 수 없습니다."))
                .getText();

        // 2) 사용자 입력
        String userInput = dto.getUserText();

        // 3) 편집 거리, 정확도, 유사도 계산
        int editDistance = calculateEditDistance(reference, userInput);
        double accuracyScore = calculateAccuracy(reference, userInput);
        double similarityScore = 1.0 - ((double) editDistance / reference.length());

        // 4) 문법 점수 초기화 (LanguageTool 분석 후 재설정)
        double grammarScore = 1.0;

        // 5) 틀린 단어 및 피드백 메시지 리스트 준비
        List<String> incorrectWords = new ArrayList<>();
        List<String> feedbackMessages = new ArrayList<>();

        try {
            // 6) LanguageTool 인스턴스 생성 (미국 영어)
            JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());

            // 7) 사용자 입력 문장(userInput)에 대해 문법 체크 수행
            List<RuleMatch> matches = langTool.check(userInput);

            // 8) 문법 오류 개수 대비 단어 수 비율로 문법 점수 계산
            grammarScore = 1.0 - ((double) matches.size() / Math.max(1, userInput.split(" ").length));

            // 9) 각 오류 매칭 항목에 대해 피드백 메시지와 틀린 단어 추출
            for (RuleMatch match : matches) {
                String ruleId = match.getRule().getId();
                System.out.println("RuleID: " + ruleId);
                String feedback;

                switch (ruleId) {
                    case "MORFOLOGIK_RULE_EN_US":
                    case "DID_YOU_MEAN":
                        String wrongWord = userInput.substring(match.getFromPos(), match.getToPos());
                        String suggestion = match.getSuggestedReplacements().isEmpty()
                                ? "수정안"
                                : match.getSuggestedReplacements().get(0);
                        feedback = "'" + wrongWord + "'는 '" + suggestion + "'의 오타일 수 있어요.";
                        break;

                    default:
                        String rawMessage = match.getMessage();
                        String cleanedMessage = cleanSuggestionTags(rawMessage);
                        String koreanMsg = koreanFeedback(ruleId);
                        if (koreanMsg == null) {
                            feedback = "문법 오류가 있습니다: " + cleanedMessage;
                        } else {
                            feedback = koreanMsg;
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

        // 10) 사용자, 콘텐츠 라이브러리 조회
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

        // 11) 문장 레벨(스피치 등급) 조회
        Sentence sentence = sentenceRepository.findById(dto.getSentenceId())
                .orElseThrow(() -> new RuntimeException("문장을 찾을 수 없습니다."));
        float level = sentence.getSentenceLevel() != null
                ? sentence.getSentenceLevel().getSpeechGrade() * 100f
                : -1f;

        // 12) DB에 채점 결과 저장 (DictationList 엔티티)
        DictationList dictation = new DictationList();
        dictation.setSentenceId(dto.getSentenceId());
        dictation.setContentsLibrary(contentsLibrary);
        dictation.setSentenceLevel(level);
        dictation.setUserText(userInput);
        dictation.setScore(accuracyScore);
        dictation.setSimilarityScore(similarityScore);
        dictation.setGrammarScore(grammarScore);                      // [ADDED] 문법 점수 저장
        dictation.setFeedback(String.join(" ", feedbackMessages));  // 기존 피드백 메시지를 하나의 문자열로 저장
        dictationListRepository.save(dictation);

        // 13) 바뀐 생성자 인자 순서: 마지막에 grammarScore 추가
        return new DictationEvalResponseDto(
                reference,
                userInput,
                accuracyScore,
                editDistance,
                incorrectWords,
                feedbackMessages,
                grammarScore                                      // [ADDED] 프론트로 전달할 문법 점수
        );
    }


    // HTML <suggestion> 태그 제거 함수
    private String cleanSuggestionTags(String input) {
        return input.replaceAll("</?suggestion>", "");
    }

    // 피드백 한글 매핑 함수
    private String koreanFeedback(String ruleId) {
        String baseRuleId = ruleId.replaceAll("(_\\d+)$", "");
        switch (baseRuleId) {
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
            case "IT_IS": return "'it's'와 'its' 사용을 혼동했을 수 있습니다.";
            case "ITS_VS_ITS": return "'it's'와 'its' 사용을 혼동했을 수 있습니다.";
            case "THERE_VS_THEIR": return "'there'와 'their'을 구분해 주세요.";
            case "AFFECT_EFFECT": return "'affect'와 'effect'의 차이를 구분해 주세요.";
            case "I_E_E_G": return "'i.e.' 또는 'e.g.' 사용 시 올바른 문법을 따르세요.";
            case "TO_TOO": return "'to'와 'too'의 의미를 구분해서 사용하세요.";
            case "EN_COMMA_BUT": return "but 앞에는 쉼표가 필요합니다.";
            case "EN_COMMA_THAT": return "that 앞에는 쉼표를 사용하지 마세요.";
            case "EN_COMMA_WHICH": return "which 앞에는 쉼표가 필요할 수 있습니다.";
            case "SENTENCE_FRAGMENT": return "문장이 불완전합니다. 주어나 동사가 빠졌을 수 있습니다.";
            case "EN_CONTRACTION_SPELLING": return "축약형의 철자에 주의하세요.";
            case "CONFUSED_WORDS": return "비슷하게 생긴 다른 단어와 혼동한 것 같습니다.";
            case "EN_PUNCTUATION_SPACE": return "구두점 뒤에는 공백이 필요합니다.";
            case "SENTENCE_END_PERIOD": return "문장 끝에 마침표가 필요합니다.";
            case "COMMA_COMPOUND_SENTENCE": return "문장 연결 시 쉼표와 접속사(and, but, or) 사용에 주의하세요.";
            case "EN_SIMPLE_PAST": return "과거형 동사 사용이 올바른지 확인하세요.";
            case "SINGULAR_VS_PLURAL": return "단수/복수 형태를 올바르게 사용하세요.";
            case "PRP_MISSING": return "주어 또는 목적어 대명사가 빠졌을 수 있습니다.";
            case "COMMA_INSIDE_QUOTE": return "따옴표 안/밖 쉼표 위치에 주의하세요.";
            case "SPELLER_RULE": return "철자 오류가 있습니다. 단어를 다시 확인하세요.";
            case "ENGLISH_WORD_REPEAT_RULE": return "단어가 반복되었습니다.";
            case "EN_COMMA_SENTENCE_TOO_LONG": return "문장이 너무 길거나 쉼표로 분리된 부분이 많습니다. 문장을 나누세요.";
            case "MORFOLOGIK_RULE_EN_US": return "철자 오류가 있습니다. 미국식 영문법 기준으로 확인하세요.";
            case "MORFOLOGIK_RULE_EN_GB": return "철자 오류가 있습니다. 영국식 영문법 기준으로 확인하세요.";
            case "EN_DETERMINER_USAGE": return "관사(the, a, an) 사용에 주의하세요.";
            case "EN_PRONOUN_USAGE": return "대명사(he, she, it, they 등) 사용이 올바른지 확인하세요.";
            case "EN_A_WHILE": return "a while과 awhile의 차이를 구분해 사용하세요.";
            case "EN_COMPOUND_WORDS": return "복합 단어 사용에 주의하세요.";
            case "EN_PASSIVE_VOICE": return "수동태 사용이 자연스러운지 확인하세요.";
            case "EN_CONDITIONALS": return "조건문(if절 등) 사용이 올바른지 확인하세요.";
            case "EN_COMMA_PARENTHESES": return "괄호 사용 시 쉼표 위치에 주의하세요.";
            case "EN_PREPOSITION": return "전치사 사용에 주의하세요.";
            case "EN_ADVERB_ADJECTIVE_ORDER": return "부사와 형용사의 순서에 주의하세요.";
            case "EN_ARTICLE_MISSING": return "관사(a, an, the)가 빠졌을 수 있습니다.";
            case "EN_PARAGRAPH_START": return "단락 시작 부분에 대문자를 사용하세요.";
            case "THIS_NNS": return "'This'는 단수명사(혹은 단수동사)와 함께 써야 합니다. 복수에는 'These'를 사용하세요.";
            case "PLURAL_VERB_AFTER_THIS": return "'This' 다음에는 단수 동사(is 등)가 와야 합니다. 복수 동사(are)는 잘못된 조합입니다.";
            case "NON3PRS_VERB": return "3인칭 단수 주어일 때 동사에 s(es)를 붙여야 합니다.";

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

    public List<DictationResultDto> getBestResultsByLibraryId(Long contentsLibraryId) {
        List<DictationList> all = dictationListRepository.findByContentsLibrary_Id(contentsLibraryId);

        return all.stream()
                .collect(Collectors.groupingBy(DictationList::getSentenceId))
                .values().stream()
                .map(list -> list.stream()
                        .max(Comparator.comparing(DictationList::getGrammarScore))
                        .orElse(null))
                .filter(Objects::nonNull)
                .map(dl -> {

                    String sentenceText = sentenceRepository.findById(dl.getSentenceId())
                            .map(Sentence::getText)
                            .orElse("");

                    return DictationResultDto.builder()
                            .sentenceId(dl.getSentenceId())
                            .userText(dl.getUserText())
                            .grammarScore(dl.getGrammarScore())
                            .similarityScore(dl.getSimilarityScore())
                            .feedback(dl.getFeedback())
                            .createdAt(dl.getCreatedAt())
                            .sentence(sentenceText)
                            .build();
                })
                .collect(Collectors.toList());
    }
    public DictationStartResponseDto getTestDictation(Long testOrder, String contentType, Long contentId) {
        List<Sentence> sentenceList = sentenceRepository.findAllWithLevel(contentType, contentId);
        if (sentenceList.isEmpty()) throw new IllegalStateException();

        int sentenceIdx = 0;
        if (testOrder == 1) sentenceIdx = sentenceList.size() / 2;
        else if (testOrder == 2) sentenceIdx = sentenceList.size() - 1;

        Sentence selected = sentenceList.get(sentenceIdx);
        String text = selected.getText();

        Optional<TtsSentence> existed = ttsSentenceRepository.findBySentenceIdAndSentenceType(selected.getId(), SentenceType.IMPORTANT);
        List<TtsSentenceItemDto> contents;

        if (existed.isPresent()) {
            TtsSentence tts = existed.get();
            contents = List.of(new TtsSentenceItemDto(text, tts.getFilePathUs(), tts.getFilePathGb(), tts.getFilePathAu()));
        } else {
            TtsSentence generated = ttsService.generateTtsFiles(selected.getId(), SentenceType.IMPORTANT, text);
            contents = List.of(new TtsSentenceItemDto(text, generated.getFilePathUs(), generated.getFilePathGb(), generated.getFilePathAu()));
        }

        return new DictationStartResponseDto(text, selected.getId(), contents, selected.getSentenceLevel().getSpeechGrade(), 0L);
    }

    public DictationEvalResponseDto evalTestDictation(DictationEvalRequestDto dto) {
        String reference = sentenceRepository.findById(dto.getSentenceId())
                .orElseThrow(IllegalStateException::new)
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

        return new DictationEvalResponseDto(reference, userInput, accuracyScore, editDistance, incorrectWords, feedbackMessages, grammarScore);
    }

}
