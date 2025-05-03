package com.eng.spring_server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Users {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    // Firebase Unique ID
    private String uid;

    // User email
    private String email;

    // User nickname
    private String nickname;

    // User profile url path
    private String profile;
}
