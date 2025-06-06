package com.eng.spring_server.repository.pronunciation;

import com.eng.spring_server.domain.pronunciation.PronunciationList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PronunciationListRepository extends JpaRepository<PronunciationList, Long> {
    List<PronunciationList> findByContentsLibrary_Id(Long contentsLibraryId);
}
