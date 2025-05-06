package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.Summarization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SummarizationRepository extends JpaRepository<Summarization, Long> {

    // 요약 문장이 이미 있는지 검사
    Optional<Summarization> findByContentTypeAndContentId(String contentType, Long contentId);

    // ContentType & ContentId 와 일치 하는 리스트 조회
    List<Summarization> findAllByContentTypeAndContentIdOrderByIdAsc(String contentType, Long contentId);

}
