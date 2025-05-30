package com.eng.spring_server.repository;

import com.eng.spring_server.domain.Users;
import com.eng.spring_server.domain.contents.ContentsLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentsLibraryRepository extends JpaRepository<ContentsLibrary, Long> {

    // UserId 와 일치 하는 모든 ContentsLibrary 조회
    List<ContentsLibrary> findByUsersId(Long userId);

    // Title 과 일치 하는 ContentsLibrary 조회
    Optional<ContentsLibrary> findByTitle(String title);

    // 엔티티 삭제
    Optional<ContentsLibrary> findByIdAndUsers_Id(Long libraryId, Long userId);

    @Query("SELECT c FROM ContentsLibrary c " +
            "WHERE c.users = :user AND c.contentsType = :contentType " +
            "AND ((:contentType = 'video' AND c.videoContents.id = :contentId) " +
            "OR (:contentType = 'text' AND c.textContents.id = :contentId))")
    Optional<ContentsLibrary> findByUserAndContentTypeAndContentId(
            @Param("user") Users user,
            @Param("contentType") String contentType,
            @Param("contentId") Long contentId
    );

    Optional<ContentsLibrary> findByUsersAndContentsTypeAndVideoContents_Id(
            Users user, String contentType, Long contentId);

    Optional<ContentsLibrary> findByUsersAndContentsTypeAndTextContents_Id(
            Users user, String contentType, Long contentId);

}
