package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.domain.contents.VideoContents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextTimeRepository extends JpaRepository<TextTime, Long> {

    // VideoContents 와 일치 하는 모든 TextTime List 조회
    List<TextTime> findByVideoContents(VideoContents videoContents);
    List<TextTime> findAllByVideoContents(VideoContents videoContents);

    // TextContents 와 일치 하는 모든 TextTime List 조회
    List<TextTime> findByTextContents(TextContents textContents);
    List<TextTime> findAllByTextContents(TextContents textContents);

    // 콘텐츠와 일치 하는 모든 데이터 삭제
    void deleteAllByVideoContents_Id(Long videoContentId);
    void deleteAllByTextContents_Id(Long textContentId);
}
