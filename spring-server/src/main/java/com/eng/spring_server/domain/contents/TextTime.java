package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TextTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float startTime;
    private float endTime;

    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne
    @JoinColumn(name = "all_contents_id")
    private AllContents allContents;
}
