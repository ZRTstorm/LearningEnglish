package com.eng.spring_server.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtsAudioResponseDto {
    private String filePathUs;
    private String filePathGb;
    private String filePathAu;
}
