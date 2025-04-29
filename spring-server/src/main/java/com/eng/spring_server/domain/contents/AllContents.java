package com.eng.spring_server.domain.contents;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AllContents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;
    private float textGrade;
    private float soundGrade;

    @Column(unique = true) // 유튜브 영상 중복 방지
    private String videoKey;

    @OneToMany(mappedBy = "allContents", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TextTime> textTimes = new ArrayList<>();
}
