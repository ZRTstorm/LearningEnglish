package com.eng.spring_server.dto.Pronunciation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PronunciationEvalRequestDto {
    private Long sentenceId;
    private Long contentsLibraryId;
}

