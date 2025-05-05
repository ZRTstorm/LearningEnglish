package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.Summarization;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.SummarizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictationService {

    private final SentenceRepository sentenceRepository;
    private final SummarizationRepository summarizationRepository;
    private final PythonApiClient pythonApiClient;
    private final TextOperationService textOperationService;

    // 문장 목록 조회
    public List<SentenceListResponseDto> getSummarySentence(SentenceListRequestDto dto) {
        List<Summarization> summaries = summarizationRepository.findAllByContentTypeAndContentId(
                dto.getContentType(), dto.getContentId());

        if (summaries.isEmpty()) {
            textOperationService.textSummarization(new ContentIdDto(dto.getContentType(), dto.getContentId()));
            summaries = summarizationRepository.findAllByContentTypeAndContentId(
                    dto.getContentType(), dto.getContentId());
        }

        if (summaries.isEmpty()) {
            throw new RuntimeException("요약문장이 생성되지 않았습니다.");
        }

        return summaries.stream()
                .map(s -> new SentenceListResponseDto(s.getId(), s.getText()))
                .collect(Collectors.toList());
    }


    public List<SentenceListResponseDto> getImportantSentences(SentenceListRequestDto dto) {
        List<Sentence> sentences = sentenceRepository.findAllByContentTypeAndContentId(
                dto.getContentType(), dto.getContentId());

        if (sentences.isEmpty()) {
            textOperationService.getImportant(new ContentIdDto(dto.getContentType(), dto.getContentId()));
            sentences = sentenceRepository.findAllByContentTypeAndContentId(dto.getContentType(), dto.getContentId());
        }

        return sentences.stream()
                .map(s -> new SentenceListResponseDto(s.getId(), s.getText()))
                .collect(Collectors.toList());
    }



    // 선택된 문장에 대한 오디오 생성
    public String generateTtsAudio(Long sentenceId, String sentenceType) {
        if ("summary".equals(sentenceType)) {
            Summarization summary = summarizationRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("요약문장이 존재하지 않습니다."));
            return pythonApiClient.requestTts(summary.getText());
        } else if ("important".equals(sentenceType)) {
            Sentence sentence = sentenceRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("중요문장이 존재하지 않습니다."));
            return pythonApiClient.requestTts(sentence.getText());
        } else {
            throw new IllegalArgumentException("지원되지 않는 문장 타입입니다.");
        }
    }


    public DictationEvalResponseDto evaluateDictation(DictationEvalRequestDto dto) {
        if ("important".equals(dto.getSentenceType())) {
            Sentence sentence = sentenceRepository.findById(dto.getSentenceId())
                    .orElseThrow(() -> new RuntimeException("해당 중요문장이 존재하지 않습니다."));
            return pythonApiClient.evaluateDictation(sentence.getText(), dto.getUserText());

        } else if ("summary".equals(dto.getSentenceType())) {
            Summarization summary = summarizationRepository.findById(dto.getSentenceId())
                    .orElseThrow(() -> new RuntimeException("해당 요약문장이 존재하지 않습니다."));
            return pythonApiClient.evaluateDictation(summary.getText(), dto.getUserText());

        } else {
            throw new IllegalArgumentException("sentenceType은 'important' 또는 'summary' 여야 합니다.");
        }
    }



}
