package com.eng.spring_server.repository;

import com.eng.spring_server.domain.word.UserWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWordRepository extends JpaRepository<UserWord, Long> {
    List<UserWord> findByUser_Uid(String uid);
    Optional<UserWord> findByUser_UidAndWord_Id(String uid, Long wordId);
}
