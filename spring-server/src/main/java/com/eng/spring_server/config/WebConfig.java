package com.eng.spring_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 예: http://localhost:8080/downloads/sentence-37_us.mp3
        registry.addResourceHandler("/downloads/**")
                .addResourceLocations("file:downloads/"); // 프로젝트 루트 기준 "downloads/" 폴더
    }
}
