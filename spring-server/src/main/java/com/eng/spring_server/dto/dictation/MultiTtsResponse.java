package com.eng.spring_server.dto.dictation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MultiTtsResponse {
    private double grade;
    private List<TtsSentenceItemDto> contents;
}
