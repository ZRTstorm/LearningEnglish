package com.eng.spring_server.service;

import com.eng.spring_server.domain.contents.*;
import com.eng.spring_server.dto.ContentIdDto;
import com.eng.spring_server.dto.InsertionQuizDto;
import com.eng.spring_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final SentenceRepository sentenceRepository;
    private final SummarizationRepository summarizationRepository;
    private final VideoContentsRepository videoContentsRepository;
    private final TextContentsRepository textContentsRepository;
    private final TextTimeRepository textTimeRepository;

    @Transactional(readOnly = true)
    public InsertionQuizDto sentenceInsertionQuiz(ContentIdDto request) {
        List<TextTime> textTimeList = new ArrayList<>();

        if (request.getContentType().equalsIgnoreCase("video")) {
            Optional<VideoContents> contents = videoContentsRepository.findById(request.getContentId());
            if (contents.isEmpty()) throw new IllegalStateException();

            textTimeList = textTimeRepository.findAllByVideoContents(contents.get());
        } else if (request.getContentType().equalsIgnoreCase("text")) {
            Optional<TextContents> contents = textContentsRepository.findById(request.getContentId());
            if (contents.isEmpty()) throw new IllegalStateException();

            textTimeList = textTimeRepository.findAllByTextContents(contents.get());
        }

        List<Sentence> sentenceList = sentenceRepository.findAllByContentTypeAndContentId(request.getContentType(), request.getContentId());
        List<Integer> itemList = shuffleNumber(sentenceList.size(), 4);

        List<Long> textTimeIds = new ArrayList<>();
        for (Integer index : itemList) {
            if (index < sentenceList.size()) {
                Sentence sentence = sentenceList.get(index);
                textTimeIds.add(sentence.getTextTimeId());
            }
        }

        List<String> textSentences = new ArrayList<>();
        List<Integer> insertNums = new ArrayList<>();
        int num = 0;

        for (TextTime tt : textTimeList) {

            textSentences.add(tt.getText());

            for (Long ids : textTimeIds) {
                if (tt.getId().equals(ids)) insertNums.add(num);
            }
            num += 1;
        }

        InsertionQuizDto dto = new InsertionQuizDto();
        dto.setSentenceList(textSentences);
        dto.setInsertNumList(insertNums);

        return dto;
    }

    @Transactional(readOnly = true)
    public List<String> summaOrderQuiz(String contentType, Long contentId) {
        List<Summarization> summaList = summarizationRepository.findAllByContentTypeAndContentIdOrderByIdAsc(contentType, contentId);

        List<String> summaTextList = summaList.stream()
                .map(Summarization::getText)
                .toList();

        int size = summaTextList.size();
        int num = 5;
        if (num >= size) {
            return new ArrayList<>(summaTextList);
        }

        List<String> selected = new ArrayList<>();
        double interval = (double) size / num;

        for (int i = 0; i < num; i++) {
            int start = (int) Math.floor(i * interval);
            int end = (int) Math.floor((i+1) * interval);
            if (end > size) end = size;

            int index = start + new Random().nextInt(Math.max(1, end - start));
            selected.add(summaTextList.get(index));
        }

        return selected;
    }

    private List<Integer> shuffleNumber(int num, int item) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);

        return numbers.subList(0, item);
    }
}
