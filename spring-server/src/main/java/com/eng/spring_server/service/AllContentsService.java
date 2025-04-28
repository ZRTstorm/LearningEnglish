package com.eng.spring_server.service;

import com.eng.spring_server.client.PythonApiClient;
import com.eng.spring_server.domain.contents.AllContents;
import com.eng.spring_server.domain.contents.AllContentsRepository;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.dto.AllContentsResponse;
import com.eng.spring_server.dto.AudioRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AllContentsService {

    private final PythonApiClient pythonApiClient;
    private final AllContentsRepository allContentsRepository;

    public Long processAudioContents(AudioRequest request) {
        // Python 서버에 요청 보내고 결과 받기
        AllContentsResponse response = pythonApiClient.requestAudioContents(request.getUrl());

        // DB에 저장할 객체 만들기
        AllContents allContents = new AllContents();
        allContents.setFilePath(response.getFile_path());
        allContents.setTextGrade(response.getText_grade());
        allContents.setSoundGrade(response.getSound_grade());

        for (var textItem : response.getText()) {
            TextTime textTime = new TextTime();
            textTime.setStartTime(textItem.getStart());
            textTime.setEndTime(textItem.getEnd());
            textTime.setText(textItem.getText());
            textTime.setAllContents(allContents);

            allContents.getTextTimes().add(textTime);
        }

        // 저장하고 id 반환
        return allContentsRepository.save(allContents).getId();
    }


    public AllContents getAudioContents(Long id) {
        return allContentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 데이터가 없습니다."));
    }
}
