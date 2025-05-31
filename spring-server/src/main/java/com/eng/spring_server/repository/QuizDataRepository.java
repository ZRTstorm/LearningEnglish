package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.QuizData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizDataRepository extends JpaRepository<QuizData, Long> {

    List<QuizData> searchAllByContentsLibraryId(Long contentsLibraryId);
}
