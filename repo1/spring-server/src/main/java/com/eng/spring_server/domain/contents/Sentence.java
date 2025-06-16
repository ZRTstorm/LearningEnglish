package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Sentence {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sentence_id")
    private Long id;

    // Content Identification
    private String contentType;
    private Long contentId;

    // TextTime ID
    private Long textTimeId;

    // The order of Sentence
    private Long textOrder;

    // sentence Text
    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @OneToOne(mappedBy = "sentence", fetch = FetchType.LAZY)
    private SentenceLevel sentenceLevel;
}
