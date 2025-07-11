package com.eng.spring_server.client;

import com.eng.spring_server.dto.*;
import com.eng.spring_server.dto.dictation.DictationEvalResponseDto;
import com.eng.spring_server.dto.dictation.MultiTtsResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PythonApiClient {

    // private final WebClient webClient = WebClient.create("http://fastapi-app:8000");

    WebClient webClient = WebClient.create("http://host.docker.internal:8000");


    public AllContentsResponse requestVideoContents(String url) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/contents/audio_all_contents")
                        .queryParam("url", url)
                        .build())
                .retrieve()
                .bodyToMono(AllContentsResponseWrapper.class)  // json 구조 때문에 래퍼 클래스 필요
                .block()
                .getContent();
    }

    public OcrContentsResponse requestTextContents(String text, String name) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/contents/ocr_all_contents")
                        .queryParam("text", text)
                        .queryParam("name", name)
                        .build())
                .retrieve()
                .bodyToMono(OcrContentsResponseWrapper.class)
                .block()
                .getContent();
    }

    public TextRankResponseDto requestTextRank(List<String> sentences) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("sentences", sentences);

        return webClient.post()
                .uri("/contents/sentence_ranked_contents")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TextRankResponseDtoWrapper.class)
                .block()
                .getContent();
    }

    public SummaTextDto requestSummarization(List<String> sentences) {
        Map<String, List<String>> body = new HashMap<>();
        body.put("sentences", sentences);

        return webClient.post()
                .uri("/contents/summarize_contents")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(SummaTextDtoWrapper.class)
                .block()
                .getContent();
    }

    public String requestTts(String text) {
        return webClient.post()
                .uri("/audio/text_to_speech")
                .bodyValue(Map.of("text", text))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public MultiTtsResponse requestMultiTts(String text, String fileName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/audio/text_to_speech")
                        .queryParam("text", text)
                        .queryParam("file_name", fileName)
                        .build())
                .retrieve()
                .bodyToMono(MultiTtsResponse.class)
                .block();
    }

    public DictationEvalResponseDto evaluateDictation(String reference, String userInput) {
        return webClient.post()
                .uri("/audio/text_grade")
                .bodyValue(Map.of("reference", reference, "hypothesis", userInput))
                .retrieve()
                .bodyToMono(DictationEvalResponseDto.class)
                .block();
    }

    public float sentenceSpeechGrade(String text) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/contents/speech_grade")
                        .queryParam("text", text)
                        .build())
                .retrieve()
                .bodyToMono(SpeechGradeWrapper.class)
                .block()
                .getGrade();
    }

    @Getter
    private static class AllContentsResponseWrapper {
        private String status;
        private AllContentsResponse content;
    }
    @Getter
    private static class OcrContentsResponseWrapper {
        private String status;
        private OcrContentsResponse content;
    }

    @Getter
    private static class TextRankResponseDtoWrapper {
        private String status;
        private TextRankResponseDto content;
    }

    @Getter
    private static class SummaTextDtoWrapper {
        private String status;
        private SummaTextDto content;
    }

    @Getter
    private static class SpeechGradeWrapper {
        private String status;
        private float grade;
    }
}
