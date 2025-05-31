package com.eng.spring_server.domain.pronunciation;

import com.eng.spring_server.domain.contents.ContentsLibrary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PronunciationList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sentenceId;

    @ManyToOne
    @JoinColumn(name = "contents_library_id")
    private ContentsLibrary contentsLibrary;

    private double accuracyScore;
    private double fluencyScore;
    private double completenessScore;
    private double pronunciationScore;

    private String feedbackMessage;

    private LocalDateTime evaluatedAt = LocalDateTime.now();
}
