package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.VideoContents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoContentsRepository extends JpaRepository<VideoContents, Long> {

    Optional<VideoContents> findByVideoKey(String videoKey); // findByKey 중복 확인
}
