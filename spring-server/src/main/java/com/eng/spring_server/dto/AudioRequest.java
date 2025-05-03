package com.eng.spring_server.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AudioRequest {
    private String url;  // 사용자 입력 유튜브 URL
    private String title;  // 사용자 입력 영상 제목
    private Long user_id;  // User ID
}
