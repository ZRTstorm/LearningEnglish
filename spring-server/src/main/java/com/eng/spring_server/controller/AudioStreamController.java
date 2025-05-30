package com.eng.spring_server.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/downloads")
public class AudioStreamController {

    // 서버에 MP3 파일이 저장되어 있는 디렉토리 경로
    private static final String AUDIO_DIR = "/home/ubuntu/my-app/downloads";

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> streamAudio(@PathVariable String filename, HttpServletRequest request) {
        File file = new File(AUDIO_DIR + filename);

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(resource);
    }
}
