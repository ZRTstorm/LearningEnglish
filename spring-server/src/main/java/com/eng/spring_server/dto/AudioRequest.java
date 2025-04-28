package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AudioRequest {
    private String url;  // 사용자 입력 유튜브 URL
}
