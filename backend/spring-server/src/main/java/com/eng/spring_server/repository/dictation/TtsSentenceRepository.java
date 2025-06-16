package com.eng.spring_server.repository.dictation;

import com.eng.spring_server.domain.contents.TtsSentence;
import com.eng.spring_server.domain.enums.SentenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TtsSentenceRepository extends JpaRepository<TtsSentence, Long> {
    Optional<TtsSentence> findBySentenceIdAndSentenceType(Long sentenceId, SentenceType sentenceType);
}

