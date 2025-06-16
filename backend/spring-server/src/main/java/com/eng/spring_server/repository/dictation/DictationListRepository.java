package com.eng.spring_server.repository.dictation;

import com.eng.spring_server.domain.dictation.DictationList;
import com.eng.spring_server.dto.CountAvgProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DictationListRepository extends JpaRepository<DictationList, Long> {
    List<DictationList> findByContentsLibrary_Id(Long contentsLibraryId);
    Optional<DictationList> findBySentenceIdAndContentsLibrary_Id(Long sentenceId, Long contentsLibraryId);

    @Query("select count(d) as count, avg(d.score) as avg from DictationList d " +
            "where d.contentsLibrary.id = :contentsLibraryId")
    CountAvgProjection findCountAndAverageScore(@Param("contentsLibraryId") Long contentsLibraryId);

}