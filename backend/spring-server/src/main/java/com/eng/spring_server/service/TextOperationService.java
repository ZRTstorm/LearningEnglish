package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.*;
import com.eng.spring_server.dto.ContentIdDto;
import com.eng.spring_server.dto.SummaTextDto;
import com.eng.spring_server.dto.TextRankResponseDto;
import com.eng.spring_server.dto.contents.TimestampDto;
import com.eng.spring_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TextOperationService {

    private final PythonApiClient pythonApiClient;
    private final SentenceRepository sentenceRepository;
    private final SummarizationRepository summarizationRepository;
    private final VideoContentsRepository videoContentsRepository;
    private final TextContentsRepository textContentsRepository;
    private final TextTimeRepository textTimeRepository;
    private final SentenceLevelRepository sentenceLevelRepository;

    @Transactional
    public void getImportant(ContentIdDto request) {
        // 이미 존재하는 중요 문장이 있는지 검사
        Optional<Sentence> sentenceOpt = sentenceRepository.findByContentTypeAndContentId(request.getContentType(), request.getContentId());
        if (sentenceOpt.isPresent()) {
            log.info("Important Sentence is Already in DB : {}, {}", request.getContentId(), request.getContentId());
            return;
        }

        // content 와 일치 하는 TextTime List 획득
        List<TextTime> timestampList = new ArrayList<>();
        if (request.getContentType().equals("video")) {
            VideoContents contents = videoContentsRepository.getReferenceById(request.getContentId());
            timestampList = textTimeRepository.findByVideoContents(contents);
        } else {
            TextContents contents = textContentsRepository.getReferenceById(request.getContentId());
            timestampList = textTimeRepository.findByTextContents(contents);
        }

        List<String> sentenceList = timestampList.stream()
                .map(TextTime::getText)
                .toList();

        TextRankResponseDto response = pythonApiClient.requestTextRank(sentenceList);

        List<Sentence> sentences = new ArrayList<>();
        for (int i = 0; i < response.getRankSentences().size(); i++) {
            TextRankResponseDto.TextRankResponse rank = response.getRankSentences().get(i);
            int index = rank.getIndex();

            TextTime textTime = timestampList.get(index);
            Long textTimeId = textTime.getId();

            Sentence sentence = new Sentence();
            sentence.setContentType(request.getContentType());
            sentence.setContentId(request.getContentId());
            sentence.setTextTimeId(textTimeId);
            sentence.setTextOrder((long) (i+1));
            sentence.setText(rank.getSentence());

            sentences.add(sentence);
        }

        sentenceRepository.saveAll(sentences);
    }

    @Transactional
    public void textSummarization(ContentIdDto request) {
        // 이미 존재하는 요약이 있는지 검사
        Optional<Summarization> summaOpt = summarizationRepository.findByContentTypeAndContentId(request.getContentType(), request.getContentId());
        if (summaOpt.isPresent()) {
            log.info("Summarization is Already in DB : {}, {}", request.getContentType(), request.getContentId());
            return;
        }

        // content 와 일치 하는 TextTime List 획득
        List<TextTime> timestampList = new ArrayList<>();
        if (request.getContentType().equals("video")) {
            VideoContents contents = videoContentsRepository.getReferenceById(request.getContentId());
            timestampList = textTimeRepository.findByVideoContents(contents);
        } else {
            TextContents contents = textContentsRepository.getReferenceById(request.getContentId());
            timestampList = textTimeRepository.findByTextContents(contents);
        }

        List<String> sentenceList = timestampList.stream()
                .map(TextTime::getText)
                .toList();

        SummaTextDto response = pythonApiClient.requestSummarization(sentenceList);

        List<Summarization> summarizes = new ArrayList<>();
        for (String item : response.getSummaSentences()) {
            Summarization summarization = new Summarization();
            summarization.setContentType(request.getContentType());
            summarization.setContentId(request.getContentId());
            summarization.setText(item);

            summarizes.add(summarization);
        }

        summarizationRepository.saveAll(summarizes);
    }

    @Transactional
    public void textSpeechLevel(ContentIdDto request) {
        List<Sentence> sentenceList = sentenceRepository.findAllByContentTypeAndContentId(request.getContentType(), request.getContentId());

        Optional<SentenceLevel> levelOpt = sentenceLevelRepository.findBySentence(sentenceList.get(0));
        if (levelOpt.isPresent()) throw new IllegalStateException("Already Level in DB");

        for (Sentence sentence : sentenceList) {
            String text = sentence.getText();
            float speechGrade = pythonApiClient.sentenceSpeechGrade(text);

            SentenceLevel sentenceLevel = new SentenceLevel();
            sentenceLevel.setSentence(sentence);
            sentenceLevel.setSpeechGrade(speechGrade);

            sentenceLevelRepository.save(sentenceLevel);
        }
    }

    @Transactional(readOnly = true)
    public List<TimestampDto> getImportantList(String contentsType, Long contentId) {
        return sentenceRepository.findSentencesByContents(contentsType, contentId);
    }

    @Transactional(readOnly = true)
    public String getSummaryText(String contentType, Long contentId) {
        List<Summarization> summaries = summarizationRepository.findAllByContentTypeAndContentIdOrderByIdAsc(contentType, contentId);

        return summaries.stream()
                .map(Summarization::getText)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.joining(" "));
    }
}
