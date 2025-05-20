package com.example.learningenglish.data.model

//서버 → 클라이언트 전달용 (response 전용)
data class LearningContentDto(
    val contentType: ContentType,        // "TEXT" or "VIDEO"
    val difficultyLevel: Int,               // 난이도 (예: 1, 2, 3)
    val contentId: String,
    val category: String,
    val originalText: String,
    val translatedText: String,
    val mapping: List<TextMapping>,
    val words: List<WordInfo>,
    val timings: List<TimingInfo>? = null // VIDEO일 경우만 존재
)

enum class ContentType(val value: String) {
    TEXT("TEXT"),
    VIDEO("VIDEO");

    companion object {
        // 문자열을 ContentType으로 변환하는 메서드
        fun fromString(value: String): ContentType {
            return values().firstOrNull { it.value == value } ?: TEXT // 기본값을 TEXT로 설정
        }
    }
}

