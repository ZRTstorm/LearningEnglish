package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.TextTime;
import com.eng.spring_server.domain.contents.VideoContents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TextTimeRepository extends JpaRepository<TextTime, Long> {

    // VideoContents 와 일치 하는 모든 TextTime List 조회
    List<TextTime> findByVideoContents(VideoContents videoContents);

    // TextContents 와 일치 하는 모든 TextTime List 조회
    List<TextTime> findByTextContents(TextContents textContents);
}
