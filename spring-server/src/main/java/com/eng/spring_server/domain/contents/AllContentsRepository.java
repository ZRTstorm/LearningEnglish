package com.eng.spring_server.domain.contents;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllContentsRepository extends JpaRepository<AllContents, Long> {
    Optional<AllContents> findByVideoKey(String videoKey); // findByKey 중복 확인
}
