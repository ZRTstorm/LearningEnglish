package com.eng.spring_server.client;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

@Service
public class PythonApiClient {

    private final WebClient webClient;

    public PythonApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://python-service:8000").build();
    }

    public String extractAudio(String url) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/extract_audio")
                        .queryParam("url", url)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기 방식으로 응답 대기
    }
}