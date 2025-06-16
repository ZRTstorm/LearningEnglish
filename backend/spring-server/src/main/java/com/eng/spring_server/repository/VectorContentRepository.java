package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.VectorContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VectorContentRepository extends JpaRepository<VectorContent, Long> {
    // ContentType 과 ContentId 가 일치 하는 데이터가 있는지 조회
    Optional<VectorContent> findByContentTypeAndContentId(String contentType, Long contentId);
}
