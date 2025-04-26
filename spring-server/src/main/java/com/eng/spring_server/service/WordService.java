package com.eng.spring_server.service;

import com.eng.spring_server.domain.word.Word;
import com.eng.spring_server.domain.word.WordRepository;
import com.eng.spring_server.dto.DictionaryResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor //final 필드에 생성자 자동 주입
public class WordService {

    private final WordRepository wordRepository;

    private final WebClient webClient = WebClient.create(); //HTTP요청(API요청)을 보내는 객체

    // application.yml에서 Google API 키 읽어오기
    @Value("${google.translation.api-key}")
    private String googleApiKey;

    // 단어 저장 메서드
    public Word saveWord(String word) {

        //DB에 단어 중복시 오류 출력
        wordRepository.findByWord(word).ifPresent(w -> {
            throw new IllegalArgumentException("이미 저장된 단어입니다.");
        });


        String meaningEnglish = fetchMeaningFromDictionary(word);// dictionaryapi.dev에서 뜻을 받아옴
        String meaningKorean = translateToKorean(meaningEnglish);   // Google cloud API로 한국어 번역

        return wordRepository.save(new Word(word, meaningKorean));  // 한국어로 번역된 뜻 저장
    }

    // 저장된 모든 단어 조회
    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    // 영어 단어 뜻 가져오기
    private String fetchMeaningFromDictionary(String word) {
        try {
            DictionaryResponse[] response = webClient.get() //word 정보를 보내서 사전을 요청하고 DictionaryResponse[] 배열에 저장
                    .uri("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                    .retrieve()
                    .bodyToMono(DictionaryResponse[].class)
                    .block();

            if (response != null && response.length > 0 &&
                    response[0].getMeanings() != null && !response[0].getMeanings().isEmpty()) {
                return response[0].getMeanings().get(0).getDefinitions().get(0).getDefinition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "뜻을 찾을 수 없습니다.";
    }

    // Google Cloud 번역 API를 이용해 영어 뜻을 한국어로 번역
    private String translateToKorean(String text) {
        try {
            String requestBody = "{"
                    + "\"q\": \"" + text + "\","
                    + "\"source\": \"en\","
                    + "\"target\": \"ko\","
                    + "\"format\": \"text\""
                    + "}";

            String response = webClient.post()
                    .uri("https://translation.googleapis.com/language/translate/v2?key=" + googleApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractTranslatedText(response);
        } catch (Exception e) {
            e.printStackTrace();
            return "번역 실패";
        }
    }

    // 위의 메서드에서 받아온 Google 번역 API 응답에서 번역된 텍스트 추출
    private String extractTranslatedText(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            return root.path("data").path("translations").get(0).path("translatedText").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "번역 실패";
        }
    }
}
