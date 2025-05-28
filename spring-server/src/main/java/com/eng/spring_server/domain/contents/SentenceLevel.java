package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class SentenceLevel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sentence_level_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_id")
    private Sentence sentence;

    private float speechGrade;
}
