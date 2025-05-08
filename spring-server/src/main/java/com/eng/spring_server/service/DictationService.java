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

        Optional<TtsSentence> existing = ttsSentenceRepository.findBySentenceIdAndSentenceType(randomSentenceId, sentenceType);
        if (existing.isPresent()) {
            TtsSentence tts = existing.get();
            String text = getTextByType(randomSentenceId, dto.getSentenceType());

            List<TtsSentenceItemDto> contents = List.of(
                    new TtsSentenceItemDto(text, null, null, tts.getFilePathUs(), tts.getFilePathGb(), tts.getFilePathAu())
            );
            return new DictationStartResponseDto(text, randomSentenceId, null, contents);
        }

        String text = getTextByType(randomSentenceId, dto.getSentenceType());
        String fileName = "sentence-" + randomSentenceId;
        MultiTtsResponse response = pythonApiClient.requestMultiTts(text, fileName);

        if (response == null || response.getContents() == null || response.getContents().isEmpty()) {
            throw new RuntimeException("TTS 변환 결과가 비어 있습니다. FastAPI 호출은 성공했지만 유효한 음성 데이터를 받지 못했습니다.");
        }

        TtsSentenceItemDto first = response.getContents().get(0);

        TtsSentence saved = ttsSentenceRepository.save(
                TtsSentence.builder()
                        .sentenceId(randomSentenceId)
                        .sentenceType(sentenceType)
                        .filePathUs(first.getFilePathUs())
                        .filePathGb(first.getFilePathGb())
                        .filePathAu(first.getFilePathAu())
                        .build()
        );

        List<TtsSentenceItemDto> contents = response.getContents().stream()
                .map(c -> new TtsSentenceItemDto(c.getText(), c.getStart(), c.getEnd(),
                        c.getFilePathUs(), c.getFilePathGb(), c.getFilePathAu()))
                .collect(Collectors.toList());

        return new DictationStartResponseDto(text, randomSentenceId, response.getGrade(), contents);
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
