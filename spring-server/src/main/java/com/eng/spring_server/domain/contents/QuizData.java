package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class QuizData {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_data_id")
    private Long id;

    private Long contentsLibraryId;

    private String quizType;

    private String originalData;

    private String userData;

    private Long score;
}
