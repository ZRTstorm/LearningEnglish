package com.eng.spring_server.controller;


import com.eng.spring_server.domain.TextTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TextTableController {

    private final TextTableRepository textTableRepository;


    @GetMapping("/text")
    String text(Model model){
        textTableRepository.findAll();
        return "hello";
    }
}
