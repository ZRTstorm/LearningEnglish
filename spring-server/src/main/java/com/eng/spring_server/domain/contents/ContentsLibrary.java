package com.eng.spring_server.domain.contents;

import com.eng.spring_server.domain.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ContentsLibrary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contents_library_id")
    private Long id;

    // 콘텐츠 타입 -> video , text
    private String contentsType;

    // User Title
    private String title;

    // 유저 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_contents_id")
    private VideoContents videoContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "text_contents_id")
    private TextContents textContents;

    @Column(nullable = false)
    private float progress = 0f;

    // 콘텐츠 추가 일자
    private LocalDateTime date;
}
