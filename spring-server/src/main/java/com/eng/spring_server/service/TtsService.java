package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.Summarization;
import com.eng.spring_server.dto.DictationRequestDto;
import com.eng.spring_server.repository.SentenceRepository;
import com.eng.spring_server.repository.SummarizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TtsService {

    private final PythonApiClient pythonApiClient;
    private final SummarizationRepository summarizationRepository;
    private final SentenceRepository sentenceRepository;

    public String generateTtsAudioBySentenceId(Long sentenceId, String sentenceType) {
        if ("summary".equals(sentenceType)) {
            Summarization summary = summarizationRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("요약문장이 존재하지 않습니다."));
            return pythonApiClient.requestTts(summary.getText());

        } else if ("important".equals(sentenceType)) {
            Sentence sentence = sentenceRepository.findById(sentenceId)
                    .orElseThrow(() -> new RuntimeException("중요문장이 존재하지 않습니다."));
            return pythonApiClient.requestTts(sentence.getText());

        } else {
            throw new IllegalArgumentException("지원되지 않는 문장 유형입니다.");
        }
    }


}
