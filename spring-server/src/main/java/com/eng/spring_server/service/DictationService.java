package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.Summarization;
import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.enums.SentenceType;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.SummarizationRepository;
import com.eng.spring_server.repository.TtsSentenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictationService {

    private final SentenceRepository sentenceRepository;
    private final SummarizationRepository summarizationRepository;
    private final PythonApiClient pythonApiClient;
    private final TextOperationService textOperationService;
    private final TtsSentenceRepository ttsSentenceRepository;

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


    public TtsAudioResponseDto handleTtsRequest(Long sentenceId, SentenceType sentenceType) {
        // 1. 기존에 TTS가 있는지 조회
        Optional<TtsSentence> existing = ttsSentenceRepository.findBySentenceIdAndSentenceType(sentenceId, sentenceType);
        if (existing.isPresent()) {
            TtsSentence tts = existing.get();
            return new TtsAudioResponseDto(tts.getFilePathUs(), tts.getFilePathGb(), tts.getFilePathAu());
        }

        // 2. 문장 텍스트 조회
        String text;
        if (sentenceType == SentenceType.IMPORTANT) {
            Sentence sentence = sentenceRepository.findById(sentenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Sentence ID"));
            text = sentence.getText();
        } else if (sentenceType == SentenceType.SUMMARY) {
            Summarization summary = summarizationRepository.findById(sentenceId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Summary ID"));
            text = summary.getText();
        } else {
            throw new IllegalArgumentException("Unsupported sentence type: " + sentenceType);
        }

        // 3. FastAPI에 TTS 생성 요청
        MultiTtsResponse response = pythonApiClient.requestMultiTts(text);

        // 4. DB에 저장
        TtsSentence saved = ttsSentenceRepository.save(
                TtsSentence.builder()
                        .sentenceId(sentenceId)
                        .sentenceType(sentenceType)
                        .filePathUs(response.getUs())
                        .filePathGb(response.getGb())
                        .filePathAu(response.getAu())
                        .build()
        );

        // 5. Dto로 응답 반환
        return new TtsAudioResponseDto(saved.getFilePathUs(), saved.getFilePathGb(), saved.getFilePathAu());
    }



    public MultiTtsResponse generateMultiTtsAudio(Long sentenceId, String sentenceType) {
        String text;

        if ("summary".equals(sentenceType)) {
            Summarization summary = summarizationRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("요약문장이 존재하지 않습니다."));
            text = summary.getText();
        } else if ("important".equals(sentenceType)) {
            Sentence sentence = sentenceRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("중요문장이 존재하지 않습니다."));
            text = sentence.getText();
        } else {
            throw new IllegalArgumentException("지원되지 않는 문장 타입입니다.");
        }

        return pythonApiClient.requestMultiTts(text);
    }


}
