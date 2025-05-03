package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.TextTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextTimeRepository extends JpaRepository<TextTime, Long> {
}
