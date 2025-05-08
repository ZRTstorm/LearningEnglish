package com.eng.spring_server.repository;

import com.eng.spring_server.domain.contents.Sentence;
import com.eng.spring_server.dto.contents.TimestampDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    // 중요 문장이 이미 있는지 검사
    Optional<Sentence> findByContentTypeAndContentId(String contentType, Long contentId);

    // ContentType & ContentId 와 일치 하는 리스트 조회
    List<Sentence> findAllByContentTypeAndContentId(String contentType, Long contentId);

    // 중요 문장 리스트 조회 -> TimeStampDto 형태로 반환
    @Query("select new com.eng.spring_server.dto.contents.TimestampDto( " +
            "CAST(tt.startTime * 1000 AS long), " +
            "CAST(tt.endTime * 1000 AS long), " +
            "tt.text, tt.translatedText) " +
            "from Sentence s join TextTime tt on s.textTimeId = tt.id " +
            "where s.contentType = :contentType and s.contentId = :contentId " +
            "order by s.textOrder asc")
    List<TimestampDto> findSentencesByContents(@Param("contentType") String contentType, @Param("contentId") Long contentId);

    @Query("SELECT s.id FROM Sentence s WHERE LOWER(s.contentType) = LOWER(:contentType) AND s.contentId = :contentId")
    List<Long> findIdsByContentTypeAndContentId(@Param("contentType") String contentType, @Param("contentId") Long contentId);


}
