package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.AllContents;
import com.eng.spring_server.domain.contents.AllContentsRepository;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.dto.AllContentsResponse;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.dto.contents.ContentsResponseDto;
import com.eng.spring_server.dto.contents.MappingDto;
import com.eng.spring_server.dto.contents.TimingDto;
import com.eng.spring_server.util.YoutubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllContentsService {

    private final PythonApiClient pythonApiClient;
    private final AllContentsRepository allContentsRepository;

        public Long processAudioContents(AudioRequest request) {
        String videoKey = YoutubeUtil.extractYoutubeVideoId(request.getUrl());

        // 영상 중복 확인
        Optional<AllContents> existing = allContentsRepository.findByVideoKey(videoKey);
        if (existing.isPresent()) {
            return existing.get().getId();
        }

        // 중복 아니면 파이썬 서버 요청
        AllContentsResponse response = pythonApiClient.requestAudioContents(request.getUrl());

        AllContents allContents = new AllContents();
        allContents.setVideoKey(videoKey); // 저장되는 부분
        allContents.setFilePath(response.getFile_path());
        allContents.setTextGrade(response.getText_grade());
        allContents.setSoundGrade(response.getSound_grade());


        allContents.setTitle(request.getTitle());
        allContents.setDifficultyLevel(0); //
        allContents.setCategory("General"); //
        allContents.setUploadedAt(LocalDateTime.now());
        allContents.setTranslatedText(response.getTranslated().toString());
        //translate text 옮기는 부분이 빠짐

        int i = 0 ;

        for (var textItem : response.getText()) {

            TextTime t = new TextTime();

            t.setTranslatedText(response.getTranslated().get(i)); // 추가
            t.setStartTime(textItem.getStart());
            t.setEndTime(textItem.getEnd());
            t.setText(textItem.getText());
            t.setAllContents(allContents);
            allContents.getTextTimes().add(t);

            i++;

        }

        return allContentsRepository.save(allContents).getId();
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


    public AllContents getAudioContents(Long id) {
        return allContentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 데이터가 없습니다."));
    }
}
