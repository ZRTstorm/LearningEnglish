package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Summarization {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summarization_id")
    private Long id;

    // Content Identification
    private String contentType;
    private Long contentId;

    // Summarization sentence
    @Column(columnDefinition = "TEXT")
    private String text;
}
