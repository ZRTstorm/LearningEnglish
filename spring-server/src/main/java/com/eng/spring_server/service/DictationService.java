package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.Summarization;
import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.dto.dictation.*;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.SummarizationRepository;
import com.eng.spring_server.repository.dictation.TtsSentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
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
        // sentenceId로 원문 텍스트 조회
        String reference;
        if ("summary".equalsIgnoreCase(dto.getSentenceType())) {
            reference = summarizationRepository.findById(dto.getSentenceId())
                    .orElseThrow(() -> new RuntimeException("요약문장이 존재하지 않습니다."))
                    .getText();
        } else {
            reference = sentenceRepository.findById(dto.getSentenceId())
                    .orElseThrow(() -> new RuntimeException("중요문장이 존재하지 않습니다."))
                    .getText();
        }

        // 정확도 평가
        String userInput = dto.getUserText();
        String ref = reference.trim().toLowerCase();
        String hyp = userInput.trim().toLowerCase();

        int maxLen = Math.max(ref.length(), hyp.length());
        int distance = new org.apache.commons.text.similarity.LevenshteinDistance().apply(ref, hyp);
        double accuracy = maxLen == 0 ? 100.0 : (1.0 - (double) distance / maxLen) * 100;
        accuracy = Math.round(accuracy * 100.0) / 100.0;

        return new DictationEvalResponseDto(reference, userInput, accuracy, distance);
    }

    public DictationStartResponseDto getRandomDictationSentence(DictationStartRequestDto dto) {
        List<Long> candidateIds;

        if ("summary".equalsIgnoreCase(dto.getSentenceType())) {
            candidateIds = summarizationRepository.findIdsByContentTypeAndContentId(
                    dto.getContentType(), dto.getContentId());
        } else {
            candidateIds = sentenceRepository.findIdsByContentTypeAndContentId(
                    dto.getContentType(), dto.getContentId());
        }

        if (candidateIds == null || candidateIds.isEmpty()) {
            throw new RuntimeException("해당 콘텐츠에 문장이 존재하지 않습니다.");
        }

        Long randomSentenceId = candidateIds.get(new Random().nextInt(candidateIds.size()));
        SentenceType sentenceType = SentenceType.valueOf(dto.getSentenceType().toUpperCase());

        // TTS가 이미 존재하는지 확인
        Optional<TtsSentence> existing = ttsSentenceRepository.findBySentenceIdAndSentenceType(randomSentenceId, sentenceType);
        String text = getTextByType(randomSentenceId, dto.getSentenceType());

        if (existing.isPresent()) {
            TtsSentence tts = existing.get();
            List<TtsSentenceItemDto> contents = List.of(
                    new TtsSentenceItemDto(text, null, null, tts.getFilePathUs(), tts.getFilePathGb(), tts.getFilePathAu())
            );
            return new DictationStartResponseDto(text, randomSentenceId, null, contents);
        }

        // 새로 TTS 생성
        TtsSentence generated = ttsService.generateTtsFiles(randomSentenceId, sentenceType, text);

        List<TtsSentenceItemDto> contents = List.of(
                new TtsSentenceItemDto(text, null, null, generated.getFilePathUs(), generated.getFilePathGb(), generated.getFilePathAu())
        );

        return new DictationStartResponseDto(text, randomSentenceId, null, contents);
    }



    private String getTextByType(Long sentenceId, String sentenceType) {
        if ("summary".equalsIgnoreCase(sentenceType)) {
            return summarizationRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("요약문장이 존재하지 않습니다."))
                    .getText();
        } else {
            return sentenceRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("중요문장이 존재하지 않습니다."))
                    .getText();
        }
    }





}
