package com.eng.spring_server.service;

import com.eng.spring_server.domain.contents.*;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final QuizDataRepository quizDataRepository;
    private final ContentsLibraryRepository contentsLibraryRepository;

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
        List<Integer> itemList = shuffleNumber(sentenceList.size(), 5);

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
    public List<IndexedText> summaOrderQuiz(String contentType, Long contentId) {
        List<Summarization> summaList = summarizationRepository.findAllByContentTypeAndContentIdOrderByIdAsc(contentType, contentId);

        List<String> summaTextList = summaList.stream()
                .map(Summarization::getText)
                .toList();

        int size = summaTextList.size();
        int num = 5;
        if (num >= size) {
            List<IndexedText> fullList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                fullList.add(new IndexedText(i, summaTextList.get(i)));
            }

            return fullList;
        }

        List<IndexedText> selected = new ArrayList<>();
        double interval = (double) size / num;

        for (int i = 0; i < num; i++) {
            int start = (int) Math.floor(i * interval);
            int end = (int) Math.floor((i+1) * interval);
            if (end > size) end = size;

            int index = start + new Random().nextInt(Math.max(1, end - start));
            selected.add(new IndexedText(index, summaTextList.get(index)));
        }

        return selected;
    }

    public Long saveQuizData(Long libraryId, String quizType, String originalData, String userData, Long score) {
        QuizData quizData = new QuizData();

        quizData.setContentsLibraryId(libraryId);
        quizData.setQuizType(quizType);
        quizData.setOriginalData(originalData);
        quizData.setUserData(userData);
        quizData.setScore(score);
        quizData.setDate(LocalDateTime.now());

        QuizData saved = quizDataRepository.save(quizData);

        return saved.getId();
    }

    public InsertionFeedbackDto insertFeedback(Long quizId) {
        Optional<QuizData> byId = quizDataRepository.findById(quizId);
        if (byId.isEmpty()) throw new IllegalStateException();
        QuizData quizData = byId.get();

        Optional<ContentsLibrary> opt = contentsLibraryRepository.findById(quizData.getContentsLibraryId());
        if (opt.isEmpty()) throw new IllegalStateException();
        ContentsLibrary contentsLibrary = opt.get();

        List<TextTime> textTimeList = new ArrayList<>();
        String contentType = null;
        if (contentsLibrary.getVideoContents() != null) {
            textTimeList = textTimeRepository.findByVideoContents(contentsLibrary.getVideoContents());
            contentType = "video";
        } else {
            textTimeList = textTimeRepository.findByTextContents(contentsLibrary.getTextContents());
            contentType = "text";
        }

        List<String> sentences = new ArrayList<>();
        for (TextTime tt : textTimeList) {
            sentences.add(tt.getText());
        }

        List<Integer> originalList = parsingStr(quizData.getOriginalData());
        List<Integer> userList = parsingStr(quizData.getUserData());

        InsertionFeedbackDto response = new InsertionFeedbackDto();
        response.setSentenceList(sentences);
        response.setOriginalNumList(originalList);
        response.setUserNumList(userList);

        return response;
    }

    public IndexedFeedback ordersFeedback(Long quizId) {
        Optional<QuizData> byId = quizDataRepository.findById(quizId);
        if (byId.isEmpty()) throw new IllegalStateException();
        QuizData quizData = byId.get();

        Optional<ContentsLibrary> opt = contentsLibraryRepository.findById(quizData.getContentsLibraryId());
        if (opt.isEmpty()) throw new IllegalStateException();
        ContentsLibrary contentsLibrary = opt.get();

        List<Summarization> summaList = null;
        if (contentsLibrary.getVideoContents() != null) {
            summaList = summarizationRepository.findAllByContentTypeAndContentId("video", contentsLibrary.getVideoContents().getId());
        } else {
            summaList = summarizationRepository.findAllByContentTypeAndContentId("text", contentsLibrary.getTextContents().getId());
        }

        List<String> summaTextList = summaList.stream()
                .map(Summarization::getText)
                .toList();

        List<Integer> originalList = parsingStr(quizData.getOriginalData());
        List<Integer> userList = parsingStr(quizData.getUserData());

        List<IndexedText> originalText = originalList.stream()
                .filter(index -> index >= 0 && index < summaTextList.size())
                .map(index -> new IndexedText(index, summaTextList.get(index)))
                .toList();

        IndexedFeedback response = new IndexedFeedback();
        response.setOriginalText(originalText);
        response.setUserOrders(userList);

        return response;
    }

    public List<QuizData> searchListQuiz(Long libraryId) {
        List<QuizData> allList = quizDataRepository.searchAllByContentsLibraryId(libraryId);

        return allList.stream()
                .collect(Collectors.groupingBy(
                        QuizData::getOriginalData,
                        Collectors.maxBy(Comparator.comparingLong(q -> q.getScore() == null ? 0 : q.getScore()))
                ))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<Integer> shuffleNumber(int num, int item) {
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers);

        return numbers.subList(0, item);
    }

    private List<Integer> parsingStr(String str) {
        return Arrays.stream(str.split("-"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
