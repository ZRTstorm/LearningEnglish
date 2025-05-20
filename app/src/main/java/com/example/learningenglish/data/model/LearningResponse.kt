package com.example.learningenglish.data.model

import com.google.gson.annotations.SerializedName

//실제 앱 코드 내에서 학습 콘텐츠 결과를 다루는 내부 모델
//→ UI 바인딩, 화면 로직용, 앱 내부 DB 저장

//서버에서 API 응답으로 오는 JSON → LearningContentDto로 파싱
//
//파싱 후 실제 앱에서 사용할 때는 LearningResponse로 변환해서 처리
data class LearningResponse(
    val contentType: String, // "TEXT" or "VIDEO"
    val contentId: String,
    val title: String,
    val difficultyLevel: Int,
    val category: String,
    val originalText: String,
    val translatedText: String,
    val mapping: List<TextMapping>,
    val timings: List<TimingInfo>?,
    val words: List<WordInfo>,
)

data class UploadResponse(
    val contentType: String,
    val contentId: String,
    val title: String,
    val difficultyLevel: Int,
    val category: String,
    val originalText: String,
    val translatedText: String,
    val mapping: List<TextMapping>,
    val timings: List<TimingInfo>?,
    val words: List<WordInfo>
)

data class AudioContent(
    val contentType: String, // "TEXT" or "VIDEO"
    val contentId: String,
    val title: String,
    val difficultyLevel: Int,
    val category: String,
    val originalText: String,
    val translatedText: String,
    val mapping: List<TextMapping>,
    val timings: List<TimingInfo>?,
    val words: List<WordInfo>?,
)


data class TextMapping(
    val contentId: String,
    val sentenceId: Int,
    val original: String,
    val translated: String
)

data class WordInfo(
    val id: String,
    val word: String,
    val meaning: String,
    val example: String? = null,
    val isFavorite: Boolean = false,  // 즐겨찾기 여부
)

data class TimingInfo(
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val segmentOriginalText: String,
    val segmentTranslatedText: String
)

data class UserLibraryContent(
    val contentId: String = "",
    val title: String? = "",              // nullable & 기본값
    val contentType: String? = "",        // nullable & 기본값
    val uploadedAt: String? = "",
    val difficultyLevel: Int = 0,
    val category: String? = ""
)


data class AllLibraryContent(
    val filePath: String,
    private var textGrade: Float = 0.0f,
    private var sound_grade: Float = 0.0f,
    var text: List<TextTimeDto>? = null,
    var translated: List<String>? = null,

    private val title: String,          // 사용자에게 표시될 제목
    private val contentType: String,    // "TEXT" or "VIDEO"
    private val difficultyLevel: Int,   // 난이도
    private val category: String,       // 카테고리

    private val contentId: String,    // 콘텐츠 ID (ex: vid001)
    private val uploadedAt: String   // 등록된 시간
)



data class AudioData(
    val url: String?,
    val title: String?,
    @SerializedName("user_id") val userId: String
)

data class OcrUploadRequest(
    val text: String,
    val title: String,
    @SerializedName("userId") val userId: String
)


data class TextData(
    val text: String?,
    val title: String?,
    val userId: String?
)

data class AudioRequest(
    val url: String,
    val title: String,
    val userId: Long
)

data class TextTimeDto(
    val start: Float,
    val end: Float,
    val text: String
    // val translatedText: String? = null
)

data class VideoDetailResponse(
    val contentType: String,
    val contentId: Int,
    val videoUrl: String,
    val title: String,
    val textGrade: Float,
    val soundGrade: Float,
    val originalText: String,
    val translatedText: String,
    val sentences: List<Sentence>,
    val words: List<WordInfo>? // null 허용
)

data class Sentence(
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val originalText: String,
    val translatedText: String
)

data class WordDetailResponse(
    val word: String,
    val phonetic: String,
    val audioUrl: String,
    val definitions: List<Definition>
)

data class Definition(
    val partOfSpeech: String,
    val definitionEn: String,
    val definitionKo: String,
    val exampleEn: String,
    val exampleKo: String,
    val synonyms: List<String>,
    val antonyms: List<String>
)









