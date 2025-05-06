package com.eng.spring_server.controller;

import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.VideoContents;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.dto.TextRequest;
import com.eng.spring_server.dto.TextsResponseDto;
import com.eng.spring_server.dto.UserLibraryResponse;
import com.eng.spring_server.dto.contents.ContentIdResponse;
import com.eng.spring_server.dto.contents.ContentsResponseDto;
import com.eng.spring_server.service.AllContentsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audio")
public class AllContentsController {

    private final AllContentsService allContentsService;

    @Operation(summary = "영상 콘텐츠 등록", description = "영상 링크를 받아 콘텐츠 자료 생성 후 저장한다")
    @PostMapping("/video/process")
    public ResponseEntity<?> processAudio(@RequestBody AudioRequest request) {
        // VideoContents 저장
        VideoContents videoContents = allContentsService.saveVideoContents(request);

        // ContentsLibrary 저장
        allContentsService.saveVideoLibrary(request.getUser_id(), request.getTitle(), videoContents);

        ContentIdResponse response = new ContentIdResponse();
        response.setContentsType("video");
        response.setContentId(videoContents.getId());

        // 응답 형태 확정 필요
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "텍스트 콘텐츠 등록", description = "텍스트를 받아 콘텐츠 자료 생성 후 저장한다")
    @PostMapping("/text/process")
    public ResponseEntity<?> processText(@RequestBody TextRequest request) {
        // TextContents 저장
        TextContents textContents = allContentsService.saveTextContents(request);

        // ContentsLibrary 저장
        allContentsService.saveTextLibrary(request.getUserId(), request.getTitle(), textContents);

        ContentIdResponse response = new ContentIdResponse();
        response.setContentsType("text");
        response.setContentId(textContents.getId());

        // 응답 형태 확정 필요
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "영상 콘텐츠 조회", description = "콘텐츠 ID 로 저장된 영상 콘텐츠 전체 정보 반환")
    @GetMapping("/video/{id}")
    public ResponseEntity<?> getAudio(@PathVariable Long id) {
        // Video Content 전체 정보 조회
        ContentsResponseDto response = allContentsService.buildContentsResponse(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "텍스트 콘텐츠 조회", description = "콘텐츠 ID 로 저장된 텍스트 콘텐츠 전체 정보 반환")
    @GetMapping("/text/{id}")
    public ResponseEntity<?> getText(@PathVariable Long id) {
        TextsResponseDto response = allContentsService.buildTextResponse(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "사용자 콘텐츠 리스트 조회", description = "사용자가 보유한 모든 콘텐츠 리스트를 조회 한다")
    @GetMapping("/library/{userId}")
    public ResponseEntity<?> getUserLibraryContents(@PathVariable Long userId) {
        // 사용자 콘텐츠 리스트 조회
        List<UserLibraryResponse> userLibrary = allContentsService.getUserLibrary(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userLibrary);
    }

    @Operation(summary = "mp3 파일 다운로드", description = "contentId를 받아 연결된 오디오(mp3) 파일을 전송 한다")
    @GetMapping("/file/{contentsType}/{contentId}")
    public ResponseEntity<Resource> downloadAudioFile(@PathVariable String contentsType, @PathVariable Long contentId) {
        try {
            Path path = allContentsService.getAudioFilePath(contentsType, contentId);
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "콘텐츠 라이브러리 삭제", description = "사용자의 콘텐츠 라이브러리를 삭제 한다")
    @DeleteMapping("/library/{userId}/{libraryId}")
    public ResponseEntity<?> deleteLibrary(@PathVariable Long userId, @PathVariable Long libraryId) {
        allContentsService.deleteContentsLibrary(userId, libraryId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "콘텐츠 삭제", description = "콘텐츠를 삭제 한다")
    @DeleteMapping("/contents/{contentType}/{contentId}")
    public ResponseEntity<?> deleteContent(@PathVariable String contentType, @PathVariable Long contentId) {
        allContentsService.deleteContents(contentType, contentId);

        return ResponseEntity.noContent().build();
    }
}
