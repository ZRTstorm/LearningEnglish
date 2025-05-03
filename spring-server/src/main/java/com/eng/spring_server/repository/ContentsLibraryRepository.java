package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.ContentsLibrary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentsLibraryRepository extends JpaRepository<ContentsLibrary, Long> {
}
