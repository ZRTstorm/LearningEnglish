package com.eng.spring_server.domain.word;

import com.eng.spring_server.domain.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @ManyToOne
    @JoinColumn(name = "user_uid", referencedColumnName = "uid") 
    private Users user; // 단어 저장한 사용자

    @ManyToOne
    @JoinColumn(name = "word_id")
    private Word word; // 연결된 단어
}