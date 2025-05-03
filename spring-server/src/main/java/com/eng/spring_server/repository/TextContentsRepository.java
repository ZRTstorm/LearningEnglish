package com.eng.spring_server.repository;


import com.eng.spring_server.domain.contents.TextContents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextContentsRepository extends JpaRepository<TextContents, Long> {
}
