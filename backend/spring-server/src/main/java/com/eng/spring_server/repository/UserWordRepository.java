package com.eng.spring_server.repository;

import com.eng.spring_server.domain.word.UserWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserWordRepository extends JpaRepository<UserWord, Long> {

    List<UserWord> findByUser_Id(Long userId);
    Optional<UserWord> findByUser_IdAndWord_Id(Long userId, Long wordId); //
    Page<UserWord> findByUser_Id(Long userId, Pageable pageable); // userId 기반으로 변경


}