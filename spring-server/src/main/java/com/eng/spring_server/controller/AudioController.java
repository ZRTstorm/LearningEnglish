package com.eng.spring_server.controller;

import com.eng.spring_server.client.PythonApiClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AudioController {

    private final PythonApiClient pythonApiClient;

    public AudioController(PythonApiClient pythonApiClient) {
        this.pythonApiClient = pythonApiClient;
    }

    @Operation(summary = "유튜브 오디오 추출", description = "유튜브 링크를 받아 mp3 파일로 변환합니다.")
    @PostMapping("/audio")
    public ResponseEntity<?> extractAudio(
            @Parameter(description = "유튜브 영상 URL")
            @RequestParam String url) {

        String result = pythonApiClient.extractAudio(url);
        return ResponseEntity.ok(result);
    }
}
