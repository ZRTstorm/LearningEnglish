package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
