package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class InsertionFeedbackDto {

    private List<String> sentenceList;
    private List<Integer> originalNumList;
    private List<Integer> userNumList;
}
