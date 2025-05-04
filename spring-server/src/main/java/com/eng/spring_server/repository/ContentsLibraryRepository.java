package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.ContentsLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentsLibraryRepository extends JpaRepository<ContentsLibrary, Long> {

    // UserId 와 일치 하는 모든 ContentsLibrary 조회
    List<ContentsLibrary> findByUsersId(Long userId);
}
