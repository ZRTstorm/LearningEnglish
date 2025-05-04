package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.domain.contents.VideoContents;
import com.eng.spring_server.dto.AllContentsResponse;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.dto.TextTimeDto;
import com.eng.spring_server.dto.UserLibraryResponse;
import com.eng.spring_server.dto.contents.ContentsResponseDto;
import com.eng.spring_server.dto.contents.TimestampDto;
import com.eng.spring_server.repository.*;
import com.eng.spring_server.util.YoutubeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            throw new IllegalStateException("The num of Eng Text & Kor Text did not same.");
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

    // ContentsLibrary -> VideoContents 추가
    public void saveVideoLibrary(Long userId, String title, VideoContents videoContents) {
        ContentsLibrary contentsLibrary = new ContentsLibrary();

        // get Proxy User
        Users users = usersRepository.getReferenceById(userId);

        contentsLibrary.setContentsType("video");
        contentsLibrary.setTitle(title);
        contentsLibrary.setUsers(users);
        contentsLibrary.setVideoContents(videoContents);

        contentsLibraryRepository.save(contentsLibrary);
    }

    // Video Contents 전체 정보 조회
    public ContentsResponseDto buildContentsResponse(Long contentId) {
        // Contents 조회
        Optional<VideoContents> byId = videoContentsRepository.findById(contentId);
        if (byId.isEmpty()) {
            log.info("Video Contents Search Exception : ID = {}", contentId);
            throw new IllegalStateException("Video content not Search");
        }

        VideoContents videoContents = byId.get();
        List<TextTime> timestampList = textTimeRepository.findByVideoContents(videoContents);
        ContentsResponseDto response = new ContentsResponseDto();

        response.setContentType("video");
        response.setContentId(videoContents.getId());
        response.setTitle(videoContents.getTitle());
        response.setTextGrade(videoContents.getTextGrade());
        response.setSoundGrade(videoContents.getSoundGrade());

        // original Long Text
        String originalText = timestampList.stream()
                .map(TextTime::getText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        response.setOriginalText(originalText);

        // Translated Long Text
        String translatedText = timestampList.stream()
                .map(TextTime::getTranslatedText)
                .map(String::trim)
                .collect(Collectors.joining(" "));
        response.setTranslatedText(translatedText);

        // TextTime -> TimestampDto
        List<TimestampDto> timeList = timestampList.stream()
                .map(t -> new TimestampDto(
                        (long) (t.getStartTime() * 1000),
                        (long) (t.getEndTime() * 1000),
                        t.getText(),
                        t.getTranslatedText()))
                .toList();
        response.setSentences(timeList);

        return response;
    }

    // User 가 보유 하는 모든 콘텐츠 조회
    public List<UserLibraryResponse> getUserLibrary(Long userId) {
        // ContentsLibrary List 조회
        List<ContentsLibrary> library = contentsLibraryRepository.findByUsersId(userId);

        return library.stream()
                .map(item -> {
                    UserLibraryResponse response = new UserLibraryResponse();

                    response.setContentType(item.getContentsType());
                    response.setTitle(item.getTitle());
                    response.setUploadDate(item.getDate());

                    if ("video".equalsIgnoreCase(item.getContentsType()) && item.getVideoContents() != null) {
                        response.setContentId(item.getVideoContents().getId());
                        response.setTextGrade(item.getVideoContents().getTextGrade());
                        response.setSoundGrade(item.getVideoContents().getSoundGrade());
                    } else if ("text".equalsIgnoreCase(item.getContentsType()) && item.getTextContents() != null) {
                        response.setContentId(item.getTextContents().getId());
                        response.setTextGrade(item.getTextContents().getTextGrade());
                        response.setSoundGrade(0.0f);
                    }
                    return response;
                }).toList();
    }

    public Path getAudioFilePath(String contentsType, Long contentId) {
        if (contentsType.equals("video")) {
            Optional<VideoContents> byId = videoContentsRepository.findById(contentId);
            if (byId.isEmpty()) throw new IllegalStateException("video content not in DB");

            return Paths.get(byId.get().getFilePath());
        } else {
            Optional<TextContents> byId = textContentsRepository.findById(contentId);
            if (byId.isEmpty()) throw new IllegalStateException("Text content not in DB");

            return Paths.get(byId.get().getFilePath());
        }
    }
}
