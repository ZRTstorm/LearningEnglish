package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class TextTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "text_time_id")
    private Long id;

    // TimeStamp
    private float startTime;
    private float endTime;

    // 원문 문장
    @Column(columnDefinition = "TEXT")
    private String text;

    // 번역 문장
    @Column(columnDefinition = "TEXT")
    private String translatedText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_contents_id")
    private VideoContents videoContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "text_contents_id")
    private TextContents textContents;
}
