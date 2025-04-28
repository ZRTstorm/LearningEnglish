package com.eng.spring_server.controller;

import com.eng.spring_server.domain.contents.AllContents;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.service.AllContentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audio")
public class AudioController {

    private final AllContentsService allContentsService;

    @PostMapping("/process")
    public Long processAudio(@RequestBody AudioRequest request) {
        return allContentsService.processAudioContents(request);
    }

    @GetMapping("/{id}")
    public AllContents getAudio(@PathVariable Long id) {
        return allContentsService.getAudioContents(id);
    }
}
