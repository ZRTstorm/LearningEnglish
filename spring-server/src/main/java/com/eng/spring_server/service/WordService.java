package com.eng.spring_server.service;

import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.word.Definition;
import com.eng.spring_server.domain.word.UserWord;
import com.eng.spring_server.domain.word.Word;
import com.eng.spring_server.domain.word.WordRepository;
import com.eng.spring_server.dto.DictionaryResponse;
import com.eng.spring_server.repository.UserWordRepository;
import com.eng.spring_server.repository.UsersRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor //final 필드에 생성자 자동 주입
public class WordService {


    private final WebClient webClient = WebClient.create(); //HTTP요청(API요청)을 보내는 객체


    private final WordRepository wordRepository;
    private final UserWordRepository userWordRepository;


    // application.yml에서 Google API 키 읽어오기
    @Value("${google.translation.api-key}")
    private String googleApiKey;

    public Word performDictionarySearch(String wordStr) {
        return wordRepository.findByWord(wordStr).orElseGet(() -> {
            Word newWord = new Word();
            newWord.setWord(wordStr);
            List<Definition> defs = fetchDefinitionsFromAPI(wordStr);
            defs.forEach(d -> d.setWord(newWord));
            newWord.setDefinitions(defs);
            return wordRepository.save(newWord);
        });
    }

    public void saveWordForUser(String wordStr, String uid) {
        Word word = performDictionarySearch(wordStr);

        userWordRepository.findByUser_UidAndWord_Id(uid, word.getId())
                .orElseGet(() -> {
                    Users user = new Users();
                    user.setUid(uid);
                    return userWordRepository.save(new UserWord(null, user, word));
                });
    }

    public List<Word> getWordsByUser(String uid) {
        return userWordRepository.findByUser_Uid(uid).stream()
                .map(UserWord::getWord)
                .toList();
    }

    public Word getWordDetail(Long wordId) {
        return wordRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 단어 없음"));
    }

    public void deleteWordForUser(String uid, Long wordId) {
        userWordRepository.findByUser_UidAndWord_Id(uid, wordId)
                .ifPresent(userWordRepository::delete);
    }

    private List<Definition> fetchDefinitionsFromAPI(String word) {
        try {
            DictionaryResponse[] response = webClient.get()
                    .uri("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                    .retrieve()
                    .bodyToMono(DictionaryResponse[].class)
                    .block();

            if (response != null && response.length > 0) {
                List<Definition> defs = new ArrayList<>();
                for (var meaning : response[0].getMeanings()) {
                    String partOfSpeech = meaning.getPartOfSpeech();
                    for (var def : meaning.getDefinitions()) {
                        Definition d = new Definition();
                        d.setDefinitionEn(def.getDefinition());
                        d.setDefinitionKo(translate(def.getDefinition()));
                        d.setExampleEn(def.getExample());
                        d.setExampleKo(def.getExample() != null ? translate(def.getExample()) : null);
                        d.setPartOfSpeech(partOfSpeech);
                        defs.add(d);
                    }
                }
                return defs;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private String translate(String text) {
        try {
            String requestBody = "{" +
                    "\"q\": \"" + text + "\"," +
                    "\"source\": \"en\"," +
                    "\"target\": \"ko\"," +
                    "\"format\": \"text\"}";

            String response = webClient.post()
                    .uri("https://translation.googleapis.com/language/translate/v2?key=" + googleApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            return root.path("data").path("translations").get(0).path("translatedText").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "번역 실패";
        }
    }
}
