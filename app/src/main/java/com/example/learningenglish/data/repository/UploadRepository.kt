package com.example.learningenglish.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.learningenglish.data.model.AddWordRequest
import com.example.learningenglish.data.model.AudioData
import com.example.learningenglish.data.model.ContentPreview
import com.example.learningenglish.data.model.DictationEvalRequest
import com.example.learningenglish.data.model.DictationEvalResponse
import com.example.learningenglish.data.model.DictationStartRequest
import com.example.learningenglish.data.model.DictationStartResponse
import com.example.learningenglish.data.model.ImportantSentence
import com.example.learningenglish.data.model.InsertionFeedbackResponse
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.data.model.VideoDetailResponse
import com.example.learningenglish.data.model.WordDetailResponse
import com.example.learningenglish.data.remote.ApiService
import com.example.learningenglish.data.remote.AuthenticatedRetrofitClient
import com.example.learningenglish.data.remote.RetrofitInstance
import com.example.learningenglish.data.remote.RetrofitInstance.api
import com.example.learningenglish.data.util.Resource
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.data.model.OcrUploadRequest
import com.example.learningenglish.data.model.OrderFeedbackResponse
import com.example.learningenglish.data.model.OrderSentence
//import com.example.learningenglish.data.model.PronunciationEvalRequest
import com.example.learningenglish.data.model.TextDetailResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import com.example.learningenglish.data.model.ContentSearchResult
import com.example.learningenglish.data.model.DictationHistoryItem
import com.example.learningenglish.data.model.InsertionQuizRetryResponse
import com.example.learningenglish.data.model.OrderQuizRetryResponse
import com.example.learningenglish.data.model.PronunciationEvalResponse
import com.example.learningenglish.data.model.PronunciationHistoryItem
//import com.example.learningenglish.data.model.PronunciationResultResponse
import com.example.learningenglish.data.model.PronunciationStartRequest
import com.example.learningenglish.data.model.PronunciationStartResponse
import com.example.learningenglish.data.model.QuizHistoryItem
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import androidx.compose.ui.platform.LocalContext
import com.example.learningenglish.data.model.ProgressUpdate
import com.example.learningenglish.data.model.SummaContentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class LearningRepository {


    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("progress_prefs", Context.MODE_PRIVATE)
    }

    fun saveProgress(contentId: Int, percent: Int) {
        if (!::prefs.isInitialized) return
        prefs.edit().putInt("progress_$contentId", percent).apply()
    }


    fun loadProgress(contentId: Int): Int {
        return prefs.getInt("progress_$contentId", 0)
    }

    fun loadAllProgress(): Map<Int, Int> {
        return prefs.all
            .filterKeys { it.startsWith("progress_") }
            .mapNotNull { entry: Map.Entry<String, *> ->
                val id = entry.key.removePrefix("progress_").toIntOrNull()
                val percent = entry.value as? Int
                if (id != null && percent != null) id to percent else null
            }.toMap()
    }

    suspend fun saveRecentLearning(userId: Int, title: String, contentType: String, contentId: Int) {
        prefs.edit().apply {
            putString("recent_title_$userId", title)
            putString("recent_type_$userId", contentType)
            putInt("recent_id_$userId", contentId)
            apply()
        }
    }

    fun getRecentLearningFlow(userId: Int): Flow<Triple<String?, String?, Int?>> = flow {
        val title = prefs.getString("recent_title_$userId", null)
        val type = prefs.getString("recent_type_$userId", null)
        val id = prefs.getInt("recent_id_$userId", -1).takeIf { it != -1 }
        emit(Triple(title, type, id))
    }


    suspend fun fetchUserLibrary(): Resource<List<UserLibraryContent>> {
        return try {
            val response = RetrofitInstance.api.getUserLibrary()
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("서버 응답 오류: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("네트워크 오류: ${e.localizedMessage}")
        }
    }

    suspend fun getLibraryForUser(userId: Int): List<UserLibraryContent> {
        val response = RetrofitInstance.api.getMyLibrary(userId)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getAllLibrary(): List<UserLibraryContent> {
        val response = RetrofitInstance.api.getAllLibrary()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    //진행도 업데이트
    suspend fun updateLibraryProgress(libraryId: Int, progress: Float): ProgressUpdate? {
        return try {
            val response = api.updateProgress(libraryId, progress)
            if (response.isSuccessful) {
                response.body()
            } else {
                null // or throw Exception
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //퀴즈 insert
    suspend fun getInsertionQuiz(contentType: String, contentId: Int) =
        api.getInsertionQuiz(contentType, contentId)

    suspend fun getLibraryId(userId: Int, contentType: String, contentId: Int): Int =
        api.getLibraryId(userId, contentType, contentId)

    suspend fun saveQuizResult(
        quizType: String,
        libraryId: Int,
        originalData: String,
        userData: String,
        score: Int
    ): Int {
        val response = api.saveQuizResult(quizType, libraryId, originalData, userData, score)
        if (response.isSuccessful) {
            val body = response.body()?.string()?.trim()
            return body?.toIntOrNull()
                ?: throw IllegalStateException("응답 본문이 비어 있거나 정수가 아님: $body")
        } else {
            throw IllegalStateException("퀴즈 저장 실패: ${response.code()}")
        }
    }


    //퀴즈 order
    suspend fun getOrderQuiz(contentType: String, contentId: Int): List<OrderSentence> {
        return api.getOrderQuiz(contentType, contentId)
    }

    //주제기반 컨텐츠 검색
    // 주제 텍스트 기반 콘텐츠 검색
    suspend fun searchByTopicText(
        userId: Int,
        topicText: String,
        startLevel: Float,
        endLevel: Float
    ): List<Pair<String, Int>> {
        return try {
            val result = RetrofitInstance.api.searchByTopicText(
                userId = userId,
                start = startLevel,
                end = endLevel,
                option = "library",
                text = topicText
            )
            result.map { it.contentType to it.contentId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSimilarContents(
        contentType: String,
        contentId: Int,
        userId: Int,
        startLevel: Float,
        endLevel: Float
    ): List<Pair<String, Int>> {
        return try {
            val result = RetrofitInstance.api.getSimilarContents(
                contentType = contentType,
                contentId = contentId,
                userId = userId,
                startLevel = startLevel,
                endLevel = endLevel,
                option = "library"
            )
            result.map { it.contentType to it.contentId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    //축약 콘텐츠 조회
    suspend fun fetchSummaContent(
        contentType: String,
        contentId: Int
    ): SummaContentResponse? {
        return try {
            RetrofitInstance.api.getSummaContent(contentType, contentId)
        } catch (e: Exception) {
            null
        }
    }

    //  콘텐츠 상세 정보 조회
    suspend fun getContentDetail(contentType: String, id: Int): ContentPreview {
        return if (contentType == "video") {
            val res =
                RetrofitInstance.api.getVideoDetail(id).body() ?: throw Exception("No content")
            ContentPreview(res.title, res.videoUrl)
        } else {
            val res = RetrofitInstance.api.getTextDetail(id).body() ?: throw Exception("No content")
            ContentPreview(res.title, res.originalText)
        }
    }


    //  라이브러리에 콘텐츠 추가
    suspend fun addToLibrary(contentType: String, contentId: Int, userId: Int) {
        if (contentType == "video") {
            val detail = RetrofitInstance.api.getVideoDetail(contentId).body() ?: return
            val data = AudioData(
                userId = userId,
                title = detail.title,
                url = detail.videoUrl
            )
            RetrofitInstance.api.uploadAudio(data)
        } else {
            val detail = RetrofitInstance.api.getTextDetail(contentId).body() ?: return
            val data = OcrUploadRequest(
                userId = userId,
                title = detail.title,
                text = detail.originalText
            )
            RetrofitInstance.api.uploadTextOcr(data)
        }
    }


    suspend fun fetchVideoDetail(id: Int): VideoDetailResponse? {
        val response = RetrofitInstance.api.getVideoDetail(id)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun fetchTextDetail(id: Int): TextDetailResponse? {
        val response = RetrofitInstance.api.getTextDetail(id)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getSummary(contentId: Int): ApiService.SummaryResponse? {
        return try {
            api.getSummary(contentId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getImportantSentences(contentId: Int): List<ImportantSentence> {
        return try {
            api.getImportantSentences(contentId)
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun fetchWordDetail(word: String): WordDetailResponse {
        return RetrofitInstance.api.getWordDetail(word)
    }

    suspend fun uploadTextContent(data: OcrUploadRequest): Response<LearningResponse> {
        return RetrofitInstance.api.uploadTextOcr(data)
    }

    suspend fun startDictation(request: DictationStartRequest): DictationStartResponse? {
        return try {
            val response = api.startDictation(request)
            Log.d("DictationAPI", "isSuccessful = ${response.isSuccessful}")
            Log.d("DictationAPI", "응답 바디: ${response.body()?.text}")
            Log.d("DictationAPI", "body = ${response.body()}")
            Log.d("DictationAPI", "errorBody = ${response.errorBody()?.string()}")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun evaluateDictation(request: DictationEvalRequest): DictationEvalResponse? {
        return try {
            val response = api.evaluateDictation(request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun downloadAudioFile(context: Context, filename: String): File? {
        return try {
            val response = RetrofitInstance.api.downloadAudioFile(filename)
            if (response.isSuccessful) {
                val inputStream = response.body()?.byteStream() ?: return null
                val file = File.createTempFile("dictation_", ".mp3", context.cacheDir)
                file.outputStream().use { inputStream.copyTo(it) }
                file
            } else null
        } catch (e: Exception) {
            Log.e("AudioDownload", "Download failed: ${e.localizedMessage}")
            null
        }
    }


    suspend fun getQuizHistory(libraryId: Int): List<QuizHistoryItem> {
        return api.getQuizHistory(libraryId)
    }


    suspend fun downloadAudioByFilename(context: Context, filename: String): File? {
        return try {
            val response = RetrofitInstance.api.downloadAudioFile(filename)
            if (response.isSuccessful) {
                val inputStream = response.body()?.byteStream() ?: return null
                val file = File.createTempFile("dictation_", ".mp3", context.cacheDir)
                file.outputStream().use { output -> inputStream.copyTo(output) }
                file
            } else null
        } catch (e: Exception) {
            Log.e("DownloadAudio", "에러: ${e.localizedMessage}")
            null
        }
    }


    suspend fun saveMp3FromUrl(url: String, fileName: String, context: Context): File? {
        val response = RetrofitInstance.api.downloadRawFile(url)
        if (response.isSuccessful) {
            val inputStream = response.body()?.byteStream()
            val file = File(context.cacheDir, fileName)
            file.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            return file
        } else return null
    }

    suspend fun evaluatePronunciationAudio(
        sentenceId: Int,
        contentsLibraryId: Int,
        audioFile: File
    ): PronunciationEvalResponse? {
        return try {
            val requestFile = audioFile
                .asRequestBody("audio/mpeg".toMediaTypeOrNull())
            val audioPart = MultipartBody.Part.createFormData(
                "audio", audioFile.name, requestFile
            )
            val response = RetrofitInstance.api.evaluatePronunciationAudio(
                sentenceId, contentsLibraryId, audioPart
            )
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("PronunciationEval", "오류: ${e.message}")
            null
        }
    }

    //발음 평가
    suspend fun startPronunciation(
        userId: Int,
        contentType: String,
        contentId: Int,
        sentenceLevel: Int
    ): PronunciationStartResponse {
        val request = PronunciationStartRequest(
            userId = userId,
            contentType = contentType,
            contentId = contentId,
            sentenceLevel = sentenceLevel
        )
        return api.startPronunciation(request)
    }

    suspend fun evaluatePronunciation(
        sentenceId: Int,
        contentsLibraryId: Int,
        audioFile: File
    ): PronunciationEvalResponse {
        val requestFile = RequestBody.create("audio/wav".toMediaTypeOrNull(), audioFile)
        //val requestFile = RequestBody.create("audio/mpeg".toMediaTypeOrNull(), audioFile)
        val audioPart = MultipartBody.Part.createFormData("audio", audioFile.name, requestFile)

        val response = api.evaluatePronunciationAudio(
            sentenceId = sentenceId,
            contentsLibraryId = contentsLibraryId,
            audio = audioPart
        )

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("API Error: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun downloadAndSaveMp3(context: Context, url: String): File? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val inputStream = response.body?.byteStream() ?: return null
                val file = File.createTempFile("dictation_audio", ".mp3", context.cacheDir)
                file.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                file
            } else null
        } catch (e: Exception) {
            Log.e("Mp3Download", "Download failed: ${e.localizedMessage}")
            null
        }
    }

    suspend fun getDictationHistory(libraryId: Int): List<DictationHistoryItem> {
        return api.getDictationHistory(libraryId)
    }

    suspend fun getPronunciationHistory(libraryId: Int): List<PronunciationHistoryItem> {
        return api.getPronunciationHistory(libraryId)
    }


    suspend fun getInsertionFeedback(quizId: Int): InsertionFeedbackResponse {
        return try {
            RetrofitInstance.api.getInsertionFeedback(quizId)
        } catch (e: Exception) {
            Log.e("Repository", "삽입 피드백 오류: ${e.message}")
            throw e
        }
    }

    suspend fun getOrderFeedback(quizId: Int): OrderFeedbackResponse {
        return try {
            RetrofitInstance.api.getOrderFeedback(quizId)
        } catch (e: Exception) {
            Log.e("Repository", "배열 피드백 오류: ${e.message}")
            throw e
        }
    }

    //재도전용
    /*
    suspend fun getInsertionQuizById(quizId: Int): InsertionQuizRetryResponse {
        return api.getInsertionQuizById(quizId)
    }

    suspend fun getOrderQuizById(quizId: Int): OrderQuizRetryResponse {
        return api.getOrderQuizById(quizId)
    }

     */
}








class WordRepository(context: Context, userPrefs: UserPreferencesDataStore) {
    private val retrofit = AuthenticatedRetrofitClient.create(context, userPrefs)
    private val securedApi = retrofit.create(ApiService::class.java)

    suspend fun getWordDetail(word: String): WordDetailResponse? {
        return try {
            securedApi.getWordDetail(word)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addToVocab(word: String, userId: Int) {
        try {
            //val request = AddWordRequest(word = word, userId = userId)
            val response = securedApi.addToVocab(word,userId)

            if (!response.isSuccessful) {
                Log.e("AddToVocab", "서버 응답 오류: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("AddToVocab", "예외 발생: ${e.message}")
        }
    }

    suspend fun fetchUserVocabPaged(userId: Int, page: Int): List<WordDetailResponse> {
        return try {
            api.getUserVocabPaged(userId, page)
        } catch (e: Exception) {
            emptyList()  // 오류 시 빈 리스트 반환
        }
    }

}



