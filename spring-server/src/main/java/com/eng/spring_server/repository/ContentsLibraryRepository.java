package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.ContentsLibrary;
import com.eng.spring_server.domain.contents.TextContents;
import com.eng.spring_server.domain.contents.VideoContents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentsLibraryRepository extends JpaRepository<ContentsLibrary, Long> {

    // UserId 와 일치 하는 모든 ContentsLibrary 조회
    List<ContentsLibrary> findByUsersId(Long userId);

    // Title 과 일치 하는 ContentsLibrary 조회
    Optional<ContentsLibrary> findByTitle(String title);

    Optional<ContentsLibrary> findByVideoContentsAndUsers_Id(VideoContents contents, Long userId);
    Optional<ContentsLibrary> findByTextContentsAndUsers_Id(TextContents contents, Long userId);

    // 엔티티 삭제
    Optional<ContentsLibrary> findByIdAndUsers_Id(Long libraryId, Long userId);
}
