package com.example.learningenglish.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

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

data class ImportantSentence(
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val originalText: String,
    val translatedText: String
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

/*
data class UserLibraryContent(
    val contentId: String = "",
    val title: String? = "",              // nullable & 기본값
    val contentType: String? = "",        // nullable & 기본값
    val uploadedAt: String? = "",
    val difficultyLevel: Int = 0,
    val category: String? = ""
)
*/

data class UserLibraryContent(
    val libraryId: Int,
    val contentType: String = "",  // "text" or "video"
    val contentId: Int,
    val userTitle: String? = "",
    val title: String? = "",
    val uploadDate: String? = "",
    val textGrade: Float = 0.0f,
    val soundGrade: Float = 0.0f,
    val progress: Float = 0.0f,
    val writeNum: Int,
    val writeScore: Float = 0.0f,
    val speechNum: Int,
    val speechScore: Float = 0.0f,
    val quizNum: Int,
    val quizScore: Float = 0.0f
)

data class SubtitleSentence(
    val originalText: String,
    val translatedText: String,
    val startTimeMillis: Long,
    val bookmarked: Boolean = false
)

data class AddWordRequest(val word: String, val userId: Int)

data class AudioData(
    val url: String?,
    val title: String?,
    @SerializedName("user_id") val userId: Int
)

data class OcrUploadRequest(
    val text: String,
    val title: String,
    @SerializedName("userId") val userId: Int
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

data class TextDetailResponse(
    val contentType: String,
    val contentId: Int,
    val title: String,
    val textGrade: Float,
    val soundGrade: Float,
    val originalText: String,
    val translatedText: String,
    val textFiles: List<TextFile>,
    val words: List<WordInfo>? // null 허용
)

data class TextFile(
    val filePath: String,
    val sentences: List<Sentence>
)

//quiz-insert
data class InsertionQuizResponse(
    val sentenceList: List<String>,
    val insertNumList: List<Int>
)

data class FeedbackQuizResponse(
    val sentenceList: List<String>,
    val originalNumList: List<Int>,
    val userNumList: List<Int>
)

data class QuizData(
    val quizId: Int,
    val sentenceList: List<String>
)

//주제기반 추천컨텐츠
data class ContentSearchResult(
    val contentType: String,
    val contentId: Int
)
data class ContentPreview(
    val title: String,
    val body: String
) : Serializable


//quiz-order
data class OrderSentence(
    val index: Int,
    val text: String
)

// InsertionFeedbackResponse
data class InsertionFeedbackResponse(
    val sentenceList: List<String>,
    val originalNumList: List<Int>,
    val userNumList: List<Int>
)

// OrderFeedbackResponse
data class OrderFeedbackResponse(
    val originalText: List<SentenceItem>,
    val userOrders: List<Int>
)

data class SentenceItem(
    val index: Int,
    val text: String
)

data class QuizHistoryItem(
    val id: Int,
    val contentsLibraryId: Int,
    val quizType: String,
    val originalData: String,
    val userData: String,
    val score: Int,
    val date: String
)

//재도전용
data class InsertionQuizRetryResponse(
    val quizId: Int,
    val sentenceList: List<String>,
    val insertNumList: List<Int>
)
data class OrderQuizRetryResponse(
    val quizId: Int,
    val sentenceList: List<String>
)




//받아쓰기 시작
data class DictationStartRequest(
    val userId: Int,
    val contentId: Int,
    val contentType: String, // "text" 또는 "video"
    //val sentenceType: String // "important" 또는 "summary"
    val sentenceLevel: Int
)

data class DictationStartResponse(
    val text: String,
    val sentenceId: Int,
    val contents: List<DictationSentence>,
    val sentenceLevel: Float,
    val contentsLibraryId: Int
)

data class DictationSentence(
    val text: String,
    val filePathUs: String,
    val filePathGb: String,
    val filePathAu: String
)

//받아쓰기 평가
data class DictationEvalRequest(
    val sentenceId: Int,
    val userText: String,
    val userId: Int,
    val contentType: String,
    val contentId: Int
)

data class DictationEvalResponse(
    val reference: String,
    val userInput: String,
    val accuracyScore: Double,
    val editDistance: Int,
    val incorrectWords: List<String>,
    val feedbackMessages: List<String>,
    val grammarScore: Double
)

// ViewModel에 받아쓰기 결과를 저장하는 전용 변수 추가
data class DictationResultData(
    val reference: String,
    val userInput: String,
    val accuracyScore: Double,
    val grammarScore: Double,
    val incorrectWords: List<String>,
    val feedbackMessages: List<String>,
    val contentId: Int,
    val contentsType: String,
    val filePaths: Map<String, String>
)



//발음 평가
data class PronunciationStartRequest(
    val userId: Int,
    val contentType: String, // "text"
    val contentId: Int,
    val sentenceLevel: Int
)

data class PronunciationStartResponse(
    val sentence: String,
    val sentenceId: Int,
    val ttsContents: List<TtsContent>,
    val level: Double,
    val contentLibraryId: Int
)

data class TtsContent(
    val text: String,
    val filePathUs: String,
    val filePathGb: String,
    val filePathAu: String
)

data class PronunciationEvalResponse(
    val accuracy: Double,
    val fluency: Double,
    val completeness: Double,
    val pronunciation: Double,
    val feedbackMessages: List<String>
)

data class DictationHistoryItem(
    val sentenceId: Int,
    val userText: String,
    val grammarScore: Double,
    val similarityScore: Double,
    val feedback: String,
    val createdAt: String
)

data class PronunciationHistoryItem(
    val sentenceId: Int,
    val accuracyScore: Double,
    val fluencyScore: Double,
    val completenessScore: Double,
    val pronunciationScore: Double,
    val feedback: FeedbackWrapper,
    val evaluatedAt: String
)

data class FeedbackWrapper(
    val raw: String // 예: "[\"\\\"took\\\"...]" 형태
)


/*
data class PronunciationResultResponse(
    val accuracy: Double,
    val fluency: Double,
    val completeness: Double,
    val pronunciation: Double,
    val feedbackMessages: List<String>
)

 */

//단어장
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









