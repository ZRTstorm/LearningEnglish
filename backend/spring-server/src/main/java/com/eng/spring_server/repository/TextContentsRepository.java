package com.eng.spring_server.repository;


import com.eng.spring_server.domain.contents.TextContents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TextContentsRepository extends JpaRepository<TextContents, Long> {

    // Title 과 일치 하는 TextContents 조회
    Optional<TextContents> findByTitle(String title);

    // Title 과 일치 하는 모든 TextContents 조회
    List<TextContents> findAllByTitle(String title);
}
