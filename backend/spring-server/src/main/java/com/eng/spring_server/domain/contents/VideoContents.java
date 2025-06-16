package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class VideoContents {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_content_id")
    private Long id;

    // Audio file 서버 경로
    private String filePath;

    // contents 난이도
    private float textGrade;
    private float soundGrade;

    // 유튜브 영상 고유 ID
    @Column(unique = true)
    private String videoKey;

    // Video 제목
    private String title;

    // 등록 시각
    private LocalDateTime uploadDate;
}
