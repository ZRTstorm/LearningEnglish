package com.eng.spring_server.client;

import com.eng.spring_server.dto.AllContentsResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class PythonApiClient {

    private final WebClient webClient = WebClient.create("http://python-service:8000");

    public AllContentsResponse requestAudioContents(String url) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/audio_all_contents")
                        .queryParam("url", url)
                        .build())
                .retrieve()
                .bodyToMono(AllContentsResponseWrapper.class)  // json 구조 때문에 래퍼 클래스 필요
                .block()
                .getContent();
    }

    @Getter
    private static class AllContentsResponseWrapper {
        private String status;
        private AllContentsResponse content;
    }
}
