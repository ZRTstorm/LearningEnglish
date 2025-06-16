package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.QuizData;
import com.eng.spring_server.dto.CountAvgProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizDataRepository extends JpaRepository<QuizData, Long> {

    List<QuizData> searchAllByContentsLibraryId(Long contentsLibraryId);

    @Query("select count(q) as count, avg(q.score) as avg from QuizData q " +
            "where q.contentsLibraryId = :contentsLibraryId")
    CountAvgProjection findCountAndAvgScore(@Param("contentsLibraryId") Long contentsLibraryId);

}
