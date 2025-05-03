package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.domain.contents.VideoContents;
import com.eng.spring_server.dto.AllContentsResponse;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.dto.TextTimeDto;
import com.eng.spring_server.dto.UserLibraryContentResponse;
import com.eng.spring_server.dto.contents.ContentsResponseDto;
import com.eng.spring_server.dto.contents.MappingDto;
import com.eng.spring_server.dto.contents.TimingDto;
import com.eng.spring_server.repository.*;
import com.eng.spring_server.util.YoutubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllContentsService {

    private final PythonApiClient pythonApiClient;
    private final VideoContentsRepository videoContentsRepository;
    private final TextContentsRepository textContentsRepository;
    private final TextTimeRepository textTimeRepository;
    private final UsersRepository usersRepository;
    private final ContentsLibraryRepository contentsLibraryRepository;

    // VideoContents 처리 작업 -> ID 반환
    @Transactional
    public VideoContents saveVideoContents(AudioRequest request) {
        // Youtube 고유 ID 추출
        String videoKey = YoutubeUtil.extractYoutubeVideoId(request.getUrl());

        // 영상 중복 확인
        Optional<VideoContents> existContents = videoContentsRepository.findByVideoKey(videoKey);
        if (existContents.isPresent()) {
            return existContents.get();
        }

        // 중복 아닐 경우 Python 서버 요청
        AllContentsResponse response = pythonApiClient.requestVideoContents(request.getUrl());

        // Video Contents 생성
        VideoContents videoContents = new VideoContents();
        videoContents.setVideoKey(videoKey);
        videoContents.setFilePath(response.getFile_path());
        videoContents.setTextGrade(response.getText_grade());
        videoContents.setSoundGrade(response.getSound_grade());

        Path filePath = Paths.get(response.getFile_path());
        String fileName = filePath.getFileName().toString();
        videoContents.setTitle(fileName);
        videoContents.setUploadDate(LocalDateTime.now());

        // Video Contents Save
        VideoContents saveContents = videoContentsRepository.save(videoContents);

        // Text - Time - Translated Mapping
        List<TextTime> timestampList = saveTextTime(response.getText(), response.getTranslated());
        for (TextTime textTime : timestampList) {
            textTime.setVideoContents(videoContents);
        }

        // TextTime Save
        textTimeRepository.saveAll(timestampList);

        return saveContents;
    }


    // TextTime 객체 구성 -> 저장
    private List<TextTime> saveTextTime(List<TextTimeDto> text, List<String> translated) {
        // List 개수 체크
        if (text.size() != translated.size()) {
            throw new IllegalStateException("The num of Eng Text & Kor Text did not same.")
        }

        List<TextTime> timestampList = new ArrayList<>();

        for (int i = 0; i < text.size(); i++) {
            TextTimeDto dto = text.get(i);
            String korText = translated.get(i);

            TextTime entity = new TextTime();
            entity.setStartTime(dto.getStart());
            entity.setEndTime(dto.getEnd());
            entity.setText(dto.getText());
            entity.setTranslatedText(korText);

            timestampList.add(entity);
        }

        return timestampList;
    }

    public void saveVideoLibrary(Long userId, VideoContents videoContents) {
        ContentsLibrary contentsLibrary = new ContentsLibrary();

        // get Proxy User
        Users users = usersRepository.getReferenceById(userId);

        contentsLibrary.setContentsType("video");
        contentsLibrary.setUsers(users);
        contentsLibrary.setVideoContents(videoContents);

        contentsLibraryRepository.save(contentsLibrary);
    }

    public ContentsResponseDto buildContentsResponse(AllContents entity) {
        String contentType = "VIDEO"; // 영상 기준
        String contentId = "vid" + String.format("%03d", entity.getId()); // 예: vid001

        String originalText = entity.getTextTimes().stream()
                .map(TextTime::getText)
                .collect(Collectors.joining("\n"));

        String translatedText = entity.getTextTimes().stream()
                .map(TextTime::getTranslatedText)
                .collect(Collectors.joining("\n"));

        List<MappingDto> mappingList = entity.getTextTimes().stream()
                .map(t -> new MappingDto(t.getText(), t.getTranslatedText()))
                .toList();

        List<TimingDto> timingList = entity.getTextTimes().stream()
                .map(t -> new TimingDto(
                        (long) (t.getStartTime() * 1000),
                        (long) (t.getEndTime() * 1000),
                        t.getText(),
                        t.getTranslatedText()))
                .toList();

        return new ContentsResponseDto(
                contentType,
                contentId,
                entity.getTitle(),
                entity.getDifficultyLevel(),
                entity.getCategory(),
                originalText,
                translatedText,
                mappingList,
                timingList,
                List.of()
        );
    }

    public List<AllContentsResponse> getAllUserContents() {
        return allContentsRepository.findAll().stream()
                .map(this::buildAllContentsResponse)
                .collect(Collectors.toList());
    }

    public AllContentsResponse buildAllContentsResponse(AllContents entity) {
        AllContentsResponse dto = new AllContentsResponse();
        dto.setTitle(entity.getTitle());
        dto.setContentType("VIDEO");
        dto.setDifficultyLevel(entity.getDifficultyLevel());
        dto.setCategory(entity.getCategory());
        dto.setContentId("vid" + String.format("%03d", entity.getId()));
        dto.setUploadedAt(entity.getUploadedAt().toString());
        return dto;
    }

    public Path getAudioFilePathByContentsId(Long id) {
        AllContents contents = allContentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘텐츠가 없습니다: " + id));
        return Paths.get(contents.getFilePath()); // DB에 저장된 절대경로를 Path 객체로 반환
    }

    public UserLibraryContentResponse convertToUserLibraryDto(AllContents entity) {
        UserLibraryContentResponse dto = new UserLibraryContentResponse();
        dto.setContentId("vid" + String.format("%03d", entity.getId()));
        dto.setTitle(entity.getTitle());
        dto.setContentType("VIDEO"); // 현재 모든 콘텐츠가 영상 기준이면 고정
        dto.setUploadedAt(entity.getUploadedAt().toString());
        dto.setDifficultyLevel(entity.getDifficultyLevel());
        dto.setCategory(entity.getCategory());
        return dto;
    }

}
