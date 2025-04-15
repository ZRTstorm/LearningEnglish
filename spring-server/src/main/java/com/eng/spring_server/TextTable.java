package com.eng.spring_server;


import jakarta.persistence.*;

@Entity
public class TextTable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(columnDefinition = "TEXT")
    public String text;

}
