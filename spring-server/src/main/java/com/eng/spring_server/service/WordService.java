package com.eng.spring_server.service;

import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.word.Definition;
import com.eng.spring_server.domain.word.UserWord;
import com.eng.spring_server.domain.word.Word;
import com.eng.spring_server.dto.DefinitionResponse;
import com.eng.spring_server.dto.WordResponse;
import com.eng.spring_server.repository.UsersRepository;
import com.eng.spring_server.repository.WordRepository;
import com.eng.spring_server.dto.DictionaryResponse;
import com.eng.spring_server.repository.UserWordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor //final 필드에 생성자 자동 주입
public class WordService {

    private final WordRepository wordRepository;
    private final UserWordRepository userWordRepository;
    private final UsersRepository usersRepository;

    private final WebClient webClient = WebClient.create(); //HTTP요청(API요청)을 보내는 객체

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

            var phoneticAndAudio = extractPhonetics(wordStr);
            newWord.setPhonetic(phoneticAndAudio[0]);
            newWord.setAudioUrl(phoneticAndAudio[1]);


            return wordRepository.save(newWord);
        });
    }

    // 발음기호와 mp3 링크 추출용 메서드
    private String[] extractPhonetics(String word) {
        try {
            DictionaryResponse[] response = webClient.get()
                    .uri("https://api.dictionaryapi.dev/api/v2/entries/en/" + word)
                    .retrieve()
                    .bodyToMono(DictionaryResponse[].class)
                    .block();

            if (response != null && response.length > 0 && response[0].getPhonetics() != null) {
                var phonetics = response[0].getPhonetics();
                for (var p : phonetics) {
                    if (p.getText() != null && p.getAudio() != null) {
                        return new String[]{p.getText(), p.getAudio()};
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{null, null};
    }

    public void saveWordForUser(String wordStr, String uid) {
        Word word = performDictionarySearch(wordStr);

        Users user = usersRepository.findByUid(uid) //
                .orElseThrow(() -> new IllegalArgumentException("해당 uid 사용자를 찾을 수 없습니다."));

        userWordRepository.findByUser_IdAndWord_Id(user.getId(), word.getId()) // ✅ userId 기준으로 연결 확인
                .orElseGet(() -> userWordRepository.save(new UserWord(null, user, word))); // 없으면 저장
    }

    public List<Word> getWordsByUser(String uid) {
        Users user = usersRepository.findByUid(uid) //
                .orElseThrow(() -> new IllegalArgumentException("해당 uid 사용자를 찾을 수 없습니다."));

        return userWordRepository.findByUser_Id(user.getId()).stream()
                .map(UserWord::getWord)
                .toList();
    }

    public Word getWordDetail(Long wordId) { //
        return wordRepository.findById(wordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 단어 없음"));
    }

    public void deleteWordForUser(String uid, Long wordId) {
        Users user = usersRepository.findByUid(uid) //
                .orElseThrow(() -> new IllegalArgumentException("해당 uid 사용자를 찾을 수 없습니다."));

        userWordRepository.findByUser_IdAndWord_Id(user.getId(), wordId)
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
                        d.setSynonyms(def.getSynonyms()); // 동의어
                        d.setAntonyms(def.getAntonyms()); // 반의어
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

    public List<Word> getPagedWordsByUser(String uid, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return userWordRepository.findByUser_Uid(uid, pageable)
                .stream()
                .map(UserWord::getWord)
                .toList();
    }


    public WordResponse convertToDto(Word word) {
        return new WordResponse(
                word.getWord(),
                word.getPhonetic(),
                word.getAudioUrl(),
                word.getDefinitions().stream()
                        .map(def -> new DefinitionResponse(
                                def.getPartOfSpeech(),
                                def.getDefinitionEn(),
                                def.getDefinitionKo(),
                                def.getExampleEn(),
                                def.getExampleKo(),
                                def.getSynonyms(),
                                def.getAntonyms()
                        ))
                        .toList()
        );
    }



}
