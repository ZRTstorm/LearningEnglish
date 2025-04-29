package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.AllContents;
import com.eng.spring_server.domain.contents.AllContentsRepository;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.dto.AllContentsResponse;
import com.eng.spring_server.dto.AudioRequest;
import com.eng.spring_server.util.YoutubeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AllContentsService {

    private final PythonApiClient pythonApiClient;
    private final AllContentsRepository allContentsRepository;

    public Long processAudioContents(AudioRequest request) {
        String videoKey = YoutubeUtil.extractYoutubeVideoId(request.getUrl());

        // 1. 이미 처리된 영상이 있는지 확인
        Optional<AllContents> existing = allContentsRepository.findByVideoKey(videoKey);
        if (existing.isPresent()) {
            return existing.get().getId();
        }

        // 2. 없으면 Python 서버로 요청
        AllContentsResponse response = pythonApiClient.requestAudioContents(request.getUrl());

        AllContents allContents = new AllContents();
        allContents.setVideoKey(videoKey); // 저장!
        allContents.setFilePath(response.getFile_path());
        allContents.setTextGrade(response.getText_grade());
        allContents.setSoundGrade(response.getSound_grade());

        for (var textItem : response.getText()) {
            TextTime t = new TextTime();
            t.setStartTime(textItem.getStart());
            t.setEndTime(textItem.getEnd());
            t.setText(textItem.getText());
            t.setAllContents(allContents);
            allContents.getTextTimes().add(t);
        }

        return allContentsRepository.save(allContents).getId();
    }



    public AllContents getAudioContents(Long id) {
        return allContentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 데이터가 없습니다."));
    }
}
