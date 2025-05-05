package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.domain.contents.VideoContents;
import com.eng.spring_server.dto.*;
import com.eng.spring_server.dto.contents.BasicResponse;
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

    // VideoContents 처리 작업
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
            textTime.setVideoContents(saveContents);
        }

        // TextTime Save
        textTimeRepository.saveAll(timestampList);

        return saveContents;
    }

    // TextContents 처리 작업
    @Transactional
    public TextContents saveTextContents(TextRequest textRequest) {
        // 콘텐츠 중복 확인
        Optional<TextContents> byTitle = textContentsRepository.findByTitle(textRequest.getTitle());
        if (byTitle.isPresent()) {
            return byTitle.get();
        }

        // 중복 아닐 경우 Python 서버 요청
        OcrContentsResponse response = pythonApiClient.requestTextContents(textRequest.getText(), textRequest.getTitle());

        List<String> regions = List.of("US", "GB", "AU");

        TextContents textCon = null;
        for (int i = 0; i < regions.size(); i++) {
            String region = regions.get(i);
            BasicResponse basic = response.getFile_text().get(i);

            TextContents contents = new TextContents();
            contents.setFilePath(basic.getFile_path());
            contents.setRegion(region);
            contents.setTextGrade(response.getText_grade());
            contents.setTitle(textRequest.getTitle());
            contents.setUploadDate(LocalDateTime.now());

            // TextContents Save
            TextContents savedContents = textContentsRepository.save(contents);

            List<TextTime> stampList = saveTextTime(response.getFile_text().get(i).getText(), response.getTranslated());
            for (TextTime textTime : stampList) {
                textTime.setTextContents(savedContents);
            }

            // TextTime Save
            textTimeRepository.saveAll(stampList);

            if (i == 0) textCon = savedContents;
        }

        return textCon;
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
    @Transactional
    public void saveVideoLibrary(Long userId, String title, VideoContents videoContents) {
        ContentsLibrary contentsLibrary = new ContentsLibrary();

        // get Proxy User
        Users users = usersRepository.getReferenceById(userId);

        contentsLibrary.setContentsType("video");
        contentsLibrary.setTitle(title);
        contentsLibrary.setUsers(users);
        contentsLibrary.setVideoContents(videoContents);
        contentsLibrary.setDate(LocalDateTime.now());

        contentsLibraryRepository.save(contentsLibrary);
    }

    @Transactional
    public void saveTextLibrary(Long userId, String title, TextContents textContents) {
        ContentsLibrary contentsLibrary = new ContentsLibrary();

        // get Proxy User
        Users users = usersRepository.getReferenceById(userId);

        contentsLibrary.setContentsType("text");
        contentsLibrary.setTitle(title);
        contentsLibrary.setUsers(users);
        contentsLibrary.setTextContents(textContents);
        contentsLibrary.setDate(LocalDateTime.now());

        contentsLibraryRepository.save(contentsLibrary);
    }

    // Video Contents 전체 정보 조회
    @Transactional(readOnly = true)
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
        response.setVideoUrl(buildYoutubeUrl(videoContents.getVideoKey()));
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

    // TextContents 전체 정보 조회
    @Transactional(readOnly = true)
    public TextsResponseDto buildTextResponse(Long contentId) {
        Optional<TextContents> byId = textContentsRepository.findById(contentId);
        if (byId.isEmpty()) throw new IllegalStateException("TextContent is not in DB");

        String title = byId.get().getTitle();
        List<TextContents> textContents = textContentsRepository.findAllByTitle(title);

        TextsResponseDto response = new TextsResponseDto();
        response.setContentType("text");
        response.setContentId(contentId);
        response.setTitle(title);
        response.setTextGrade(textContents.get(0).getTextGrade());

        List<TextsResponseDto.TextFile> textFileList = new ArrayList<>();
        for (int i = 0; i < textContents.size(); i++) {
            List<TextTime> timestampList = textTimeRepository.findByTextContents(textContents.get(i));

            if (i == 0) {
                String originalText = timestampList.stream()
                        .map(TextTime::getText)
                        .map(String::trim)
                        .collect(Collectors.joining(" "));
                response.setOriginalText(originalText);

                String translatedText = timestampList.stream()
                        .map(TextTime::getTranslatedText)
                        .map(String::trim)
                        .collect(Collectors.joining(" "));
                response.setTranslatedText(translatedText);
            }

            TextsResponseDto.TextFile item = new TextsResponseDto.TextFile();
            item.setFilePath(textContents.get(i).getFilePath());
            // TextTime -> TimestampDto
            List<TimestampDto> timeList = timestampList.stream()
                    .map(t -> new TimestampDto(
                            (long) (t.getStartTime() * 1000),
                            (long) (t.getEndTime() * 1000),
                            t.getText(),
                            t.getTranslatedText()))
                    .toList();
            item.setSentences(timeList);

            textFileList.add(item);
        }
        response.setTextFiles(textFileList);

        return response;
    }

    // User 가 보유 하는 모든 콘텐츠 조회
    @Transactional(readOnly = true)
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

    // 오디오 파일 경로 조회
    @Transactional(readOnly = true)
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

    private String buildYoutubeUrl(String videoId) {
        return "https://www.youtube.com/watch?v=" + videoId;
    }
}
