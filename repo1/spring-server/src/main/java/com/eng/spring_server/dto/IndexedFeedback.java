package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class IndexedFeedback {

    List<IndexedText> originalText;
    List<Integer> userOrders;
}
