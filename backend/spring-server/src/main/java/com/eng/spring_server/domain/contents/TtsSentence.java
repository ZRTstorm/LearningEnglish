package com.eng.spring_server.domain.contents;

import com.eng.spring_server.domain.enums.SentenceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tts_sentence")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtsSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sentenceId; // Summarization or Sentence ID

    @Enumerated(EnumType.STRING)
    private SentenceType sentenceType; // "SUMMARY" or "IMPORTANT"

    private String filePathUs;
    private String filePathGb;
    private String filePathAu;
}
