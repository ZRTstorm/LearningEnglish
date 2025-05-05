package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    // 중요 문장이 이미 있는지 검사
    Optional<Sentence> findByContentTypeAndContentId(String contentType, Long contentId);

    // ContentType & ContentId 와 일치 하는 리스트 조회
    List<Sentence> findAllByContentTypeAndContentId(String contentType, Long contentId);
}
