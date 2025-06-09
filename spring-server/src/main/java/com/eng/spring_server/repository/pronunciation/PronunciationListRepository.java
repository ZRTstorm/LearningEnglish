package com.eng.spring_server.repository.pronunciation;

import com.eng.spring_server.domain.pronunciation.PronunciationList;
import com.eng.spring_server.dto.CountAvgProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PronunciationListRepository extends JpaRepository<PronunciationList, Long> {
    List<PronunciationList> findByContentsLibrary_Id(Long contentsLibraryId);

    @Query("select count(p) as count, avg(p.pronunciationScore) as avg from PronunciationList p " +
            "where p.contentsLibrary.id = :contentsLibraryId")
    CountAvgProjection findCountAndAvgScores(@Param("contentsLibraryId") Long contentsLibraryId);
}