package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.domain.contents.SentenceLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SentenceLevelRepository extends JpaRepository<SentenceLevel, Long> {

    Optional<SentenceLevel> findBySentence(Sentence sentence);
}
