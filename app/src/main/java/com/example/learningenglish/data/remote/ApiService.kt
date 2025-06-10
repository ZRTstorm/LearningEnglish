package com.example.learningenglish.data.remote

import com.example.learningenglish.data.model.AddWordRequest
import com.example.learningenglish.data.model.AudioContent
import com.example.learningenglish.data.model.AudioData
import com.example.learningenglish.data.model.ContentSearchResult
import com.example.learningenglish.data.model.DictationEvalRequest
import com.example.learningenglish.data.model.DictationEvalResponse
import com.example.learningenglish.data.model.DictationHistoryItem
import com.example.learningenglish.data.model.DictationStartRequest
import com.example.learningenglish.data.model.DictationStartResponse
import com.example.learningenglish.data.model.ImportantSentence
import com.example.learningenglish.data.model.InsertionFeedbackResponse
import com.example.learningenglish.data.model.InsertionQuizResponse
import com.example.learningenglish.data.model.LearningResponse

import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.data.model.VideoDetailResponse
import com.example.learningenglish.data.model.WordDetailResponse
import com.example.learningenglish.data.model.WordInfo
import com.example.learningenglish.data.model.OcrUploadRequest
import com.example.learningenglish.data.model.OrderFeedbackResponse
import com.example.learningenglish.data.model.OrderSentence
import com.example.learningenglish.data.model.PronunciationEvalResponse
import com.example.learningenglish.data.model.PronunciationHistoryItem
//import com.example.learningenglish.data.model.PronunciationResultResponse
import com.example.learningenglish.data.model.PronunciationStartRequest
import com.example.learningenglish.data.model.PronunciationStartResponse
import com.example.learningenglish.data.model.QuizHistoryItem
import com.example.learningenglish.data.model.SummaContentResponse
import com.example.learningenglish.data.model.TextDetailResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

// ApiService.kt
interface ApiService {

    // 단어 목록 조회
    @GET("api/words")
    suspend fun getWordList(): Response<List<WordInfo>>

    @GET("/text/important/text/{id}")
    suspend fun getImportantSentences(@Path("id") contentId: Int): List<ImportantSentence>

    @GET("/text/summarization/text/{id}")
    suspend fun getSummary(@Path("id") contentId: Int): SummaryResponse

    data class SummaryResponse(val summaryText: String)


    // 단어 저장
    @POST("api/words")
    suspend fun saveWord(
        @Body word: WordInfo
    ): Response<WordInfo>  // 또는 응답 형식에 맞는 Response 객체

    // 영상 등록 (음성 파일 처리)
    @POST("api/audio/video/process")
    suspend fun uploadAudio(
        @Body audioData: AudioData
    ): Response<LearningResponse>

    @POST("/api/audio/text/process")
    suspend fun uploadTextOcr(@Body data: OcrUploadRequest): Response<LearningResponse>


    // 개별 영상 조회
    @GET("api/audio/{id}")
    suspend fun getAudio(
        @Path("id") audioId: String
    ): Response<AudioContent>


    // 사용자의 전체 콘텐츠 조회 (모든 콘텐츠)
    @GET("api/audio/library")
    suspend fun getUserLibrary(): Response<List<UserLibraryContent>>

    // 사용자의 전체 콘텐츠 조회 (모든 콘텐츠)
    @GET("api/audio/library")
    suspend fun getAllLibrary(): Response<List<UserLibraryContent>>

    @GET("api/audio/library/{userId}")
    suspend fun getMyLibrary(@Path("userId") userId: Int): Response<List<UserLibraryContent>>


    // mp3 파일 다운로드
    @GET("api/audio/file/{contentsType}/{contentId}")
    suspend fun downloadFile(
        @Path("contentsType") contentsType: String,
        @Path("contentId") contentId: Int
    ): Response<ResponseBody>  // ResponseBody로 파일 다운로드 처리



    @GET("/api/audio/video/{id}")
    suspend fun getVideoDetail(@Path("id") id: Int): Response<VideoDetailResponse>

    @GET("/api/audio/text/{id}")
    suspend fun getTextDetail(@Path("id") id: Int): Response<TextDetailResponse>

    //주제 기반 추천컨텐츠
    @GET("/embedding/search/texts/{userId}")
    suspend fun searchByTopicText(
        @Path("userId") userId: Int,
        @Query("start") start: Float,
        @Query("end") end: Float,
        @Query("option") option: String,
        @Query("text") text: String
    ): List<ContentSearchResult>

    //유사 컨텐츠
    @GET("/embedding/search/service/{contentType}/{contentId}/{userId}")
    suspend fun getSimilarContents(
        @Path("contentType") contentType: String,
        @Path("contentId") contentId: Int,
        @Path("userId") userId: Int,
        @Query("start") startLevel: Float,
        @Query("end") endLevel: Float,
        @Query("option") option: String = "library"
    ): List<ContentSearchResult>

    //축약 콘텐츠 조회
    @GET("/api/audio/summaContent/{contentType}/{contentId}")
    suspend fun getSummaContent(
        @Path("contentType") contentType: String,
        @Path("contentId") contentId: Int
    ): SummaContentResponse

    //퀴즈 삽입
    @GET("quiz/insertion/{contentType}/{contentId}")
    suspend fun getInsertionQuiz(
        @Path("contentType") contentType: String,
        @Path("contentId") contentId: Int
    ): InsertionQuizResponse

    @GET("api/audio/library/ID/{userId}/{contentType}/{contentId}")
    suspend fun getLibraryId(
        @Path("userId") userId: Int,
        @Path("contentType") contentType: String,
        @Path("contentId") contentId: Int
    ): Int

    @GET("quiz/save/{quizType}/{libraryId}")
    suspend fun saveQuizResult(
        @Path("quizType") quizType: String,
        @Path("libraryId") libraryId: Int,
        @Query("originalData") originalData: String,
        @Query("userData") userData: String,
        @Query("score") score: Int
    ): Response<ResponseBody>

    //퀴즈 배열
    @GET("quiz/orders/{contentType}/{contentId}")
    suspend fun getOrderQuiz(
        @Path("contentType") contentType: String,
        @Path("contentId") contentId: Int
    ): List<OrderSentence>


    @GET("quiz/feedback/insertion/{quizId}")
    suspend fun getInsertionFeedback(@Path("quizId") quizId: Int): InsertionFeedbackResponse

    @GET("quiz/feedback/orders/{quizId}")
    suspend fun getOrderFeedback(@Path("quizId") quizId: Int): OrderFeedbackResponse

    @GET("quiz/search/list/{libraryId}")
    suspend fun getQuizHistory(
        @Path("libraryId") libraryId: Int
    ): List<QuizHistoryItem>





    @POST("dictation/start")
    suspend fun startDictation(@Body request: DictationStartRequest): Response<DictationStartResponse>

    @POST("dictation/eval")
    suspend fun evaluateDictation(@Body request: DictationEvalRequest): Response<DictationEvalResponse>

    @GET("downloads/{filename}")
    suspend fun downloadAudioFile(
        @Path("filename") filename: String
    ): Response<ResponseBody>

    // 받아쓰기 기록 조회
    @GET("dictation/list/{libraryId}")
    suspend fun getDictationHistory(@Path("libraryId") libraryId: Int): List<DictationHistoryItem>

    // 발음 평가 기록 조회
    @GET("pronunciation/list/{libraryId}")
    suspend fun getPronunciationHistory(@Path("libraryId") libraryId: Int): List<PronunciationHistoryItem>



    @GET("/api/words/detail")
    suspend fun getWordDetail(@Query("word") word: String): WordDetailResponse

    @POST("/api/words/add")
    suspend fun addToVocab(
        @Query("word") word: String,
        @Query("userId") userId: Int
    ): Response<Unit>

    @GET("/api/words/user/{userId}/paged")
    suspend fun getUserVocabPaged(
        @Path("userId") userId: Int,
        @Query("page") page: Int
    ): List<WordDetailResponse>



    @POST("/pronunciation/start")
    suspend fun startPronunciation(
        @Body request: PronunciationStartRequest
    ): PronunciationStartResponse

    @Multipart
    @POST("pronunciation/evaluate")
    suspend fun evaluatePronunciationAudio(
        @Query("sentenceId") sentenceId: Int,
        @Query("contentsLibraryId") contentsLibraryId: Int,
        @Part audio: MultipartBody.Part
    ): Response<PronunciationEvalResponse>

    /*
    @GET("/api/words/user/{uid}")
    suspend fun getUserVocab(@Path("uid") uid: String): List<WordDetailResponse>*/



    @Streaming
    @GET
    suspend fun downloadRawFile(@Url fileUrl: String): Response<ResponseBody>




}

interface AuthApiService {
    @POST("/users/auth/login")
    suspend fun sendIdToken(
        @Body tokenRequest: TokenRequest
    ): Response<UserResponse>
}

data class TokenRequest(val idToken: String)
data class UserResponse(val userId: Int)




/*
interface ApiService {

    // 업로드 API (업로드 시 바로 학습 콘텐츠 데이터 반환)
    @Multipart
    @POST("/upload")
    suspend fun uploadContent(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody
    ): Response<LearningResponse>  // or OcrResponse depending on backend behavior

    // 개별 학습 콘텐츠 가져오기
    @GET("/learning/{contentId}")
    suspend fun getLearningContent(
        @Path("contentId") id: String
    ): Response<LearningResponse>

    @FormUrlEncoded
    @POST("/upload/link")
    suspend fun uploadLink(
        @Field("link") link: String,
        @Field("title") title: String
    ): Response<LearningResponse>

    // 내 라이브러리 콘텐츠 리스트
    @GET("/learning/user-library")
    suspend fun getUserLibrary(): Response<List<UserLibraryContent>>

    // 4. 즐겨찾기 단어 목록 조회 (선택사항)
    @GET("/user/favorites")
    suspend fun getFavoriteWords(): Response<List<WordInfo>>

    // 5. 즐겨찾기 추가/삭제 (선택사항)
    @POST("/user/favorites/{wordId}")
    suspend fun toggleFavorite(
        @Path("wordId") wordId: String
    ): Response<Unit>
}
 */
