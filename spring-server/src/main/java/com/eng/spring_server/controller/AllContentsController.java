package com.eng.spring_server.controller;

import com.eng.spring_server.domain.contents.AllContents;
import com.eng.spring_server.dto.AllContentsResponse;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.dto.contents.ContentsResponseDto;
import com.eng.spring_server.service.AllContentsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audio")
public class AllContentsController {

    private final AllContentsService allContentsService;

    @Operation(summary = "영상 등록", description = "유튜브 URL을 받아 텍스트로 변환하고 DB에 저장한 후 콘텐츠 정보를 반환합니다.")
    @PostMapping("/process")
    public ResponseEntity<ContentsResponseDto> processAudio(@RequestBody AudioRequest request) {
        Long id = allContentsService.processAudioContents(request);
        AllContents result = allContentsService.getAudioContents(id);
        ContentsResponseDto response = allContentsService.buildContentsResponse(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "영상 조회", description = "ID를 기반으로 저장된 콘텐츠 전체 정보 반환")
    @GetMapping("/{id}")
    public ResponseEntity<ContentsResponseDto> getAudio(@PathVariable Long id) {
        AllContents result = allContentsService.getAudioContents(id);
        ContentsResponseDto response = allContentsService.buildContentsResponse(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 콘텐츠 전체 조회", description = "사용자가 등록한 콘텐츠 목록을 반환합니다.")
    @GetMapping("/library")
    public ResponseEntity<List<AllContentsResponse>> getAllUserContents() {
        List<AllContentsResponse> responseList = allContentsService.getAllUserContents();
        return ResponseEntity.ok(responseList);
    }
}
