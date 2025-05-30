package com.eng.spring_server.repository.dictation;

import com.eng.spring_server.domain.dictation.DictationList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DictationListRepository extends JpaRepository<DictationList, Long> {
    List<DictationList> findByContentsLibrary_Id(Long contentsLibraryId);
    Optional<DictationList> findBySentenceIdAndContentsLibrary_Id(Long sentenceId, Long contentsLibraryId);

}