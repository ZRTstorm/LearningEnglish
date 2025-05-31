package com.eng.spring_server.repository.pronunciation;

import com.eng.spring_server.domain.pronunciation.PronunciationList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PronunciationListRepository extends JpaRepository<PronunciationList, Long> {
}