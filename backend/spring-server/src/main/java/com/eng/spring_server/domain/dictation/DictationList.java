package com.eng.spring_server.domain.dictation;

import com.eng.spring_server.domain.contents.ContentsLibrary;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictationList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contents_library_id")
    private ContentsLibrary contentsLibrary;

    private Long sentenceId;

    @Column
    private Float sentenceLevel;

    @Column(columnDefinition = "TEXT")
    private String userText;

    private Double score;
    private Double similarityScore;
    private Double grammarScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
