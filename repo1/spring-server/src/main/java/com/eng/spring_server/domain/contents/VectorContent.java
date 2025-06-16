package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class VectorContent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vector_id")
    private Long id;

    private String contentType;
    private Long contentId;
}
