package com.example.learningenglish.data.remote

import com.example.learningenglish.data.model.AudioContent
import com.example.learningenglish.data.model.AudioData
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.model.OcrResponse
import com.example.learningenglish.data.model.UploadResponse
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.data.model.VideoDetailResponse
import com.example.learningenglish.data.model.WordDetailResponse
import com.example.learningenglish.data.model.WordInfo
import com.example.learningenglish.data.model.OcrUploadRequest
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

// ApiService.kt
interface ApiService {

    // 단어 목록 조회
    @GET("api/words")
    suspend fun getWordList(): Response<List<WordInfo>>

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
    suspend fun getMyLibrary(@Path("userId") userId: String): Response<List<UserLibraryContent>>


    // mp3 파일 다운로드
    @GET("api/audio/file")
    suspend fun downloadMp3(
        @Query("fileId") fileId: String
    ): Response<ResponseBody>  // ResponseBody로 파일 다운로드 처리

    // 즐겨찾기 단어 목록 조회
    @GET("api/user/favorites")
    suspend fun getFavoriteWords(): Response<List<WordInfo>>

    // 즐겨찾기 추가/삭제
    @POST("api/user/favorites/{wordId}")
    suspend fun toggleFavorite(
        @Path("wordId") wordId: String
    ): Response<Unit>

    // 학습 콘텐츠 조회
    /*
    @GET("api/audio/video/{id}") // 실제 API 경로를 확인하고 수정해야 함
    suspend fun getLearningContent(
        @Path("id") contentId: String // Path로 contentId를 전달
    ): Response<LearningResponse> // LearningResponse 반환
     */

    @GET("/api/audio/video/{id}")
    suspend fun getVideoDetail(@Path("id") id: Int): Response<VideoDetailResponse>

    @GET("/api/words/detail")
    suspend fun getWordDetail(@Query("word") word: String): WordDetailResponse

    @POST("/api/words/add")
    suspend fun addToVocab(@Body body: Map<String, String>): Response<Unit>

    /*
    @GET("/api/words/user/{uid}")
    suspend fun getUserVocab(@Path("uid") uid: String): List<WordDetailResponse>*/

    @GET("/api/words/user/{uid}/paged")
    suspend fun getUserVocabPaged(
        @Path("uid") uid: String,
        @Query("page") page: Int
    ): List<WordDetailResponse>



}

interface AuthApiService {
    @POST("/users/auth/login")
    suspend fun sendIdToken(
        @Body tokenRequest: TokenRequest
    ): Response<UserResponse>
}

data class TokenRequest(val idToken: String)
data class UserResponse(val userId: String)




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
