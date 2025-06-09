package com.example.learningenglish.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningenglish.data.model.DictationEvalRequest
import com.example.learningenglish.data.model.DictationEvalResponse
import com.example.learningenglish.data.model.DictationHistoryItem
import com.example.learningenglish.data.model.DictationResultData
import com.example.learningenglish.data.model.DictationStartRequest
import com.example.learningenglish.data.model.DictationStartResponse
//import com.example.learningenglish.data.model.EvalResultResponse
import com.example.learningenglish.data.model.ImportantSentence
import com.example.learningenglish.data.model.InsertionFeedbackResponse
import com.example.learningenglish.data.model.InsertionQuizRetryResponse
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.model.OcrUploadRequest
import com.example.learningenglish.data.model.OrderFeedbackResponse
import com.example.learningenglish.data.model.OrderQuizRetryResponse
import com.example.learningenglish.data.model.PronunciationEvalResponse
import com.example.learningenglish.data.model.PronunciationHistoryItem
//import com.example.learningenglish.data.model.PronunciationResultResponse
import com.example.learningenglish.data.model.PronunciationStartRequest
import com.example.learningenglish.data.model.PronunciationStartResponse
import com.example.learningenglish.data.model.QuizData
import com.example.learningenglish.data.model.QuizHistoryItem
import com.example.learningenglish.data.model.SubtitleSentence
import com.example.learningenglish.data.model.TextDetailResponse
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.data.model.VideoDetailResponse
import com.example.learningenglish.data.model.WordDetailResponse
import com.example.learningenglish.data.remote.RetrofitInstance.api
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.data.util.Resource
//import com.example.learningenglish.data.util.saveDownloadedFile
import com.example.learningenglish.ui.auth.AttendancePreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate

enum class SortOption {
    TITLE, UPLOADED_AT
}

enum class FilterOption {
    ALL, VIDEO_ONLY, IMAGE_ONLY
}

class LearningViewModel(
    private val repository: LearningRepository,
    private val repositoryW: WordRepository,
    private val attendancePrefs: AttendancePreferencesDataStore
) : ViewModel() {

    var lastDictationResult by mutableStateOf<DictationResultData?>(null)

    private val _learningContent = MutableStateFlow<LearningResponse?>(null)
    val learningContent: StateFlow<LearningResponse?> = _learningContent

    private val _userLibrary = MutableStateFlow<List<UserLibraryContent>>(emptyList())
    val userLibrary: StateFlow<List<UserLibraryContent>> = _userLibrary

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 정렬 및 필터링 상태
    private val _sortOption = MutableStateFlow(SortOption.TITLE)
    val sortOption: StateFlow<SortOption> = _sortOption

    private val _filterOption = MutableStateFlow(FilterOption.ALL)
    val filterOption: StateFlow<FilterOption> = _filterOption

    private val _videoDetail = MutableStateFlow<VideoDetailResponse?>(null)
    val videoDetail: StateFlow<VideoDetailResponse?> = _videoDetail

    private val _videoLibrary = MutableStateFlow<List<VideoDetailResponse>>(emptyList())
    val videoLibrary: StateFlow<List<VideoDetailResponse>> = _videoLibrary

    private val _textDetail = MutableStateFlow<TextDetailResponse?>(null)
    val textDetail: StateFlow<TextDetailResponse?> = _textDetail

    private val _textLibrary = MutableStateFlow<List<TextDetailResponse>>(emptyList())
    val textLibrary: StateFlow<List<TextDetailResponse>> = _textLibrary

    private val _selectedEvalSentence = MutableStateFlow<String?>(null)
    val selectedEvalSentence: StateFlow<String?> = _selectedEvalSentence

    //private val _evalResult = MutableStateFlow<EvalResultResponse?>(null)
    //val evalResult: StateFlow<EvalResultResponse?> = _evalResult

    private val repo = LearningRepository()

    private val _startResult = MutableStateFlow<PronunciationStartResponse?>(null)
    val startResult: StateFlow<PronunciationStartResponse?> = _startResult

    private val _evalResult = MutableStateFlow<PronunciationEvalResponse?>(null)
    val evalResult: StateFlow<PronunciationEvalResponse?> = _evalResult

    //음성 파일 경로 저장용
    var lastPronunciationFilePaths by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    fun startPronunciation(userId: Int, contentType: String, contentId: Int, sentenceLevel: Int) {
        viewModelScope.launch {
            try {
                val response = repo.startPronunciation(userId, contentType, contentId, sentenceLevel)
                _startResult.value = response

                // TTS 파일 경로 추출 및 저장
                val tts = response.ttsContents.firstOrNull()
                lastPronunciationFilePaths = mapOf(
                    "US" to (tts?.filePathUs ?: ""),
                    "GB" to (tts?.filePathGb ?: ""),
                    "AU" to (tts?.filePathAu ?: "")
                )
            } catch (e: Exception) {
                // 에러 로깅 또는 UI 피드백
                _startResult.value = null
                lastPronunciationFilePaths = emptyMap()
            }
        }
    }


    fun evaluatePronunciation(sentenceId: Int, contentsLibraryId: Int, audioFile: File) {
        viewModelScope.launch {
            try {
                val response = repo.evaluatePronunciation(sentenceId, contentsLibraryId, audioFile)
                _evalResult.value = response
            } catch (e: Exception) {
                _evalResult.value = null
            }
        }
    }


    fun setEvalSentence(sentence: String) {
        _selectedEvalSentence.value = sentence
    }

    /*
    fun evaluatePronunciation(contentId: Int, text: String, audioFile: File) {
        viewModelScope.launch {
            try {
                Log.d("PronEval", "audio file path: ${audioFile.absolutePath}")
                Log.d("PronEval", "audio file size: ${audioFile.length()} bytes")

                val audioRequest = audioFile
                    .asRequestBody("audio/mp4".toMediaTypeOrNull())

                val multipartBody = MultipartBody.Part.createFormData(
                    name = "audio",
                    filename = audioFile.name,
                    body = audioRequest
                )

                val response = repository.evaluatePronunciation(
                    contentId,
                    text,
                    multipartBody
                )

                Log.d("PronEval", "📡 response code: ${response.code()}")
                Log.d("PronEval", "📡 response body: ${response.body()}")


                if (response.isSuccessful) {
                    val result = response.body()
                    _evalResult.value = result
                } else {
                    Log.e("PronEval", "실패: ${response.code()}, ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PronEval", "에러: ${e.localizedMessage}")
            }
        }
    }

     */

    private val _summaryText = MutableStateFlow<String?>(null)
    val summaryText: StateFlow<String?> = _summaryText

    private val _importantSentences = MutableStateFlow<List<ImportantSentence>>(emptyList())
    val importantSentences: StateFlow<List<ImportantSentence>> = _importantSentences

    fun loadSummary(contentId: Int) {
        viewModelScope.launch {
            _summaryText.value = repository.getSummary(contentId)?.summaryText
        }
    }

    fun loadImportantSentences(contentId: Int) {
        viewModelScope.launch {
            _importantSentences.value = repository.getImportantSentences(contentId)
        }
    }





    fun loadVideoDetail(id: Int) {
        viewModelScope.launch {
            val result = repository.fetchVideoDetail(id)
            _videoDetail.value = result
        }
    }

    fun loadTextDetail(id: Int) {
        viewModelScope.launch {
            val result = repository.fetchTextDetail(id)
            _textDetail.value = result
        }
    }

    fun loadAllTextContents(maxId: Int) {
        viewModelScope.launch {
            val results = mutableListOf<TextDetailResponse>()
            for (id in 1..maxId) {
                repository.fetchTextDetail(id)?.let { results.add(it) }
            }
            _textLibrary.value = results
        }
    }

    fun loadLibraryForUser(userId: Int) {
        viewModelScope.launch {
            val result = repository.getLibraryForUser(userId)
            _userLibrary.value = result
        }
    }

    fun loadAllVideoContents(maxId: Int) {
        viewModelScope.launch {
            val results = mutableListOf<VideoDetailResponse>()
            for (id in 1..maxId) {
                repository.fetchVideoDetail(id)?.let { results.add(it) }
            }
            _videoLibrary.value = results
        }
    }

    /*
    suspend fun downloadMp3(contentId: Int, context: Context): File? {
        val response = api.downloadFile("text", contentId)
        return saveDownloadedFile(response, "audio_text_${contentId}.mp3", context)
    }

     */

    suspend fun getAudioFile(context: Context, audioUrl: String): File? {
        return repository.downloadAndSaveMp3(context, audioUrl)
    }

    suspend fun fetchDictationAudio(context: Context, filename: String): File? {
        return repository.downloadAudioByFilename(context, filename)
    }

    fun loadLibrary() {
        viewModelScope.launch {
            when (val result = LearningRepository().fetchUserLibrary()) {
                is Resource.Success -> {
                    _userLibrary.value = result.data
                    filterAndSortLibrary()
                    _errorMessage.value = null
                }
                is Resource.Error -> {
                    _userLibrary.value = emptyList()
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun loadLibraryText() {
        viewModelScope.launch {
            when (val result = LearningRepository().fetchUserLibrary()) {
                is Resource.Success -> {
                    _userLibrary.value = result.data
                    filterAndSortLibrary()
                    _errorMessage.value = null
                }
                is Resource.Error -> {
                    _userLibrary.value = emptyList()
                    _errorMessage.value = result.message
                }
            }
        }
    }

    suspend fun startDictation(request: DictationStartRequest): DictationStartResponse? {
        return repository.startDictation(request)
    }

    suspend fun evaluateDictation(request: DictationEvalRequest): DictationEvalResponse? {
        return repository.evaluateDictation(request)
    }

    /*
    suspend fun startPronunciation(request: PronunciationStartRequest): PronunciationStartResponse? {
        return repository.startPronunciation(request)
    }

    suspend fun evaluatePronunciation(): PronunciationEvalResponse? {
        return repository.evaluatePronunciation(request)
    }

     */

    fun getAudioFileFromFilename(context: Context, filename: String): File? {
        return runBlocking {
            repository.downloadAudioFile(context, filename)
        }
    }



    private val _quizHistory = MutableStateFlow<List<QuizHistoryItem>>(emptyList())
    val quizHistory: StateFlow<List<QuizHistoryItem>> = _quizHistory

    fun loadQuizHistory(userId: Int, contentType: String, contentId: Int) {
        viewModelScope.launch {
            try {
                val libraryId = repo.getLibraryId(userId, contentType, contentId)
                _quizHistory.value = repo.getQuizHistory(libraryId)
            } catch (e: Exception) {
                Log.e("QuizHistory", "Error: ${e.message}")
            }
        }
    }



    private val _dictationHistory = MutableStateFlow<List<DictationHistoryItem>>(emptyList())
    val dictationHistory: StateFlow<List<DictationHistoryItem>> = _dictationHistory

    private val _pronunciationHistory = MutableStateFlow<List<PronunciationHistoryItem>>(emptyList())
    val pronunciationHistory: StateFlow<List<PronunciationHistoryItem>> = _pronunciationHistory

    fun loadDictationHistory(userId: Int, contentType: String, contentId: Int) {
        viewModelScope.launch {
            try {
                val libraryId = repo.getLibraryId(userId, contentType, contentId)
                _dictationHistory.value = repo.getDictationHistory(libraryId)
            } catch (e: Exception) {
                Log.e("HistoryLoad", "Error loading histories: ${e.message}")
            }
        }
    }

    fun loadPronunciationHistory(userId: Int, contentType: String, contentId: Int) {
        viewModelScope.launch {
            try {
                val libraryId = repo.getLibraryId(userId, contentType, contentId)
                _pronunciationHistory.value = repo.getPronunciationHistory(libraryId)
            } catch (e: Exception) {
                Log.e("HistoryLoad", "Error loading histories: ${e.message}")
            }
        }
    }



    //주제 기반 컨텐츠 추천
    suspend fun searchContentByText(
        userId: Int,
        topicText: String,
        startLevel: Float,
        endLevel: Float
    ): List<Pair<String, Int>> {
        return repository.searchByTopicText(userId, topicText, startLevel, endLevel)
    }

    private val _similarContents = MutableStateFlow<List<Pair<String, Int>>>(emptyList())
    val similarContents: StateFlow<List<Pair<String, Int>>> = _similarContents

    fun loadSimilarContents(
        contentType: String,
        contentId: Int,
        userId: Int,
        startLevel: Float,
        endLevel: Float
    ) {
        viewModelScope.launch {
            val result = repository.getSimilarContents(contentType, contentId, userId, startLevel, endLevel)
            _similarContents.value = result

        }
    }


    fun addContentToLibrary(contentType: String, contentId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                repository.addToLibrary(contentType, contentId, userId)
            } catch (e: Exception) {
                Log.e("LibraryAdd", "에러: ${e.localizedMessage}")
            }
        }
    }

    fun searchTopicContents(
        userId: Int,
        topicText: String,
        startLevel: Float,
        endLevel: Float,
        onResult: (List<Pair<String, Int>>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = api.searchByTopicText(
                    userId = userId,
                    start = startLevel,
                    end = endLevel,
                    option = "library",
                    text = topicText
                )
                if (response.isSuccessful) {
                    val result = response.body()  // [{"contentType": "video", "contentId": 5}, ...]
                    //onResult(result ?: emptyList())
                } else {
                    onError(Exception("검색 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }



    // 정렬 옵션 변경
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        filterAndSortLibrary()  // 정렬 변경 시 필터링 및 정렬 실행
    }

    // 필터 옵션 변경
    fun setFilterOption(option: FilterOption) {
        _filterOption.value = option
        filterAndSortLibrary()  // 필터링 옵션 변경 시 필터링 및 정렬 실행
    }

    // 필터링 및 정렬된 라이브러리 목록 업데이트
    private fun filterAndSortLibrary() {
        _userLibrary.value = _userLibrary.value
            .filter { filterLibraryItems(it, _filterOption.value) }
            .sortedWith(getComparatorForSortOption(_sortOption.value))
    }

    // 필터링 함수
    private fun filterLibraryItems(item: UserLibraryContent, filterOption: FilterOption): Boolean {
        return when (filterOption) {
            FilterOption.ALL -> true
            FilterOption.VIDEO_ONLY -> item.contentType == "VIDEO"
            FilterOption.IMAGE_ONLY -> item.contentType == "IMAGE"
        }
    }

    // 정렬 함수
    private fun getComparatorForSortOption(sortOption: SortOption): Comparator<UserLibraryContent> {
        return when (sortOption) {
            SortOption.TITLE -> compareBy { it.title }
            //SortOption.CATEGORY -> compareBy { it.category }
            SortOption.UPLOADED_AT -> compareBy { it.uploadDate }
        }
    }

    private val _progressMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val progressMap: StateFlow<Map<Int, Int>> = _progressMap

    fun initRepository(context: Context) {
        repository.init(context)
        loadSavedProgress()
    }

    fun setProgressForContent(contentId: Int, progressPercent: Int) {
        repository.saveProgress(contentId, progressPercent)

        _progressMap.update { current ->
            current + (contentId to progressPercent)
        }
    }

    fun loadSavedProgress() {
        val saved = repository.loadAllProgress()
        _progressMap.value = saved
    }

    fun getProgressForContent(contentId: Int): Int {
        return _progressMap.value[contentId] ?: 0
    }

    suspend fun loadBookmarkedProgress(contentId: Int, sentences: List<SubtitleSentence>) {
        val bookmarkedIndex = sentences.indexOfFirst { it.bookmarked }
        val totalSentences = sentences.size
        val percent = if (bookmarkedIndex >= 0) {
            ((bookmarkedIndex + 1).toFloat() / totalSentences * 100).toInt()
        } else 0
        setProgressForContent(contentId, percent)
    }

    private val _selectedWordInfo = MutableStateFlow<WordDetailResponse?>(null)
    val selectedWordInfo: StateFlow<WordDetailResponse?> = _selectedWordInfo

    fun loadWordDetail(word: String) {
        viewModelScope.launch {
            val response = repositoryW.getWordDetail(word)
            _selectedWordInfo.value = response
        }
    }

    fun addWordToUserVocab(word: String, userId: Int) {
        viewModelScope.launch {
            repositoryW.addToVocab(word, userId) // POST or PUT, 서버 API에 따라
        }
    }

    private val _pagedUserVocab = MutableStateFlow<List<WordDetailResponse>>(emptyList())
    val pagedUserVocab: StateFlow<List<WordDetailResponse>> = _pagedUserVocab

    fun loadUserVocabPaged(userId: Int, page: Int) {
        viewModelScope.launch {
            val result = repositoryW.fetchUserVocabPaged(userId, page)
            _pagedUserVocab.value = result
        }
    }

    fun submitOcrText(
        text: String,
        title: String,
        userId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val data = OcrUploadRequest(text, title, userId)
                val response = repository.uploadTextContent(data)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.message())
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "네트워크 오류")
            }
        }
    }

    // 퀴즈 - insert
    var sentenceList by mutableStateOf(listOf<String>())
    var insertNumList by mutableStateOf(listOf<Int>())
    var userAnswers by mutableStateOf(mutableListOf<Int>())
    var currentQuizIndex by mutableStateOf(0)

    /*
    suspend fun loadQuiz(contentType: String, contentId: Int) {
        val response = repository.getInsertionQuiz(contentType, contentId)
        sentenceList = response.sentenceList
        insertNumList = response.insertNumList
        userAnswers = MutableList(insertNumList.size) { -1 }
    }

     */

    suspend fun loadQuiz(userId: Int, contentType: String, contentId: Int): QuizData {
        val response = repository.getInsertionQuiz(contentType, contentId)
        insertNumList = response.insertNumList.toMutableList()
        sentenceList = response.sentenceList
        val quizId = repository.getLibraryId(userId, contentType, contentId)
        return QuizData(quizId, response.sentenceList)
    }

    fun saveQuiz(userId: Int, contentType: String, contentId: Int) {
        viewModelScope.launch {
            saveQuizResult(userId, contentType, contentId)
        }
    }

    fun setUserAnswer(index: Int, answer: Int) {
        userAnswers[index] = answer
    }

    fun calculateScore(): Int {
        val correct = insertNumList.zip(userAnswers).count { it.first == it.second }
        return (correct * 100) / insertNumList.size
    }

    suspend fun saveQuizResult(userId: Int, contentType: String, contentId: Int, quizType: String = "insertion") {
        val libraryId = repository.getLibraryId(userId, contentType, contentId)
        val original = insertNumList.joinToString("-")
        val user = userAnswers.joinToString("-")
        val score = calculateScore()
        repository.saveQuizResult(quizType, libraryId, original, user, score)
    }

    // 퀴즈 - order
    // 문장 배열 퀴즈 로드
    /*
    suspend fun loadOrderQuiz(contentType: String, contentId: Int) {
        val response = repository.getOrderQuiz(contentType, contentId)
        sentenceList = response.map { it.text }
        insertNumList = response.map { it.index }
        userAnswers = insertNumList.shuffled().toMutableList()
    }*/


    suspend fun loadOrderQuiz(userId: Int, contentType: String, contentId: Int): QuizData {
        val result = repository.getOrderQuiz(contentType, contentId)
        insertNumList = result.map { it.index }.toMutableList()
        sentenceList = result.map { it.text }
        val quizId = repository.getLibraryId(userId, contentType, contentId)
        return QuizData(quizId, sentenceList)
    }

    fun updateUserOrder(newOrder: List<Int>) {
        userAnswers = newOrder.toMutableList()
    }

    fun calculateOrderScore(): Int {
        val correct = insertNumList.zip(userAnswers).count { it.first == it.second }
        return (correct * 100) / insertNumList.size
    }

    suspend fun saveOrderQuizResult(userId: Int, contentType: String, contentId: Int) {
        val libraryId = repository.getLibraryId(userId, contentType, contentId)
        val original = insertNumList.joinToString("-")
        val user = userAnswers.joinToString("-")
        val score = calculateOrderScore()
        repository.saveQuizResult("summaOrders", libraryId, original, user, score)
    }

    //퀴즈 피드백
    var orderFeedback by mutableStateOf<OrderFeedbackResponse?>(null)
    var insertionFeedback by mutableStateOf<InsertionFeedbackResponse?>(null)

    suspend fun loadOrderFeedback(quizId: Int): OrderFeedbackResponse {
        val feedback = repo.getOrderFeedback(quizId)
        orderFeedback = feedback
        return feedback
    }

    suspend fun loadInsertionFeedback(quizId: Int): InsertionFeedbackResponse {
        val feedback = repo.getInsertionFeedback(quizId)
        insertionFeedback = feedback
        return feedback
    }

    /*
    //재도전용
    suspend fun loadQuizById(quizId: Int): InsertionQuizRetryResponse {
        return repo.getInsertionQuizById(quizId)
    }

    suspend fun loadOrderQuizById(quizId: Int): OrderQuizRetryResponse {
        return repo.getOrderQuizById(quizId)
    }

     */


    /*
    // 삽입 피드백
    suspend fun loadInsertionFeedback(quizId: Int): List<Pair<Int, String>> {
        val response = repository.getInsertionFeedback(quizId)

        // 필드 존재 확인
        val sentenceList = response.sentenceList
        val originalNumList = response.originalNumList
        userAnswers = response.userNumList.toMutableList()

        // 화면에 보여줄 (정답순) 문장 구성
        return originalNumList.map { index -> index to sentenceList[index] }
    }

    // 배열 피드백
    suspend fun loadOrderFeedback(quizId: Int): List<Pair<Int, String>> {
        val response = repository.getOrderFeedback(quizId)

        val orderSentences = response.originalText
        userAnswers = response.userOrders.toMutableList()

        return orderSentences.map { it.index to it.text }
    }

     */




    // 출석 관련 -------------------------------
    private val _checkedDates = MutableStateFlow<List<LocalDate>>(emptyList())
    val checkedDates: StateFlow<List<LocalDate>> = _checkedDates

    private val _consecutiveDays = MutableStateFlow(0)
    val consecutiveDays: StateFlow<Int> = _consecutiveDays

    private val _todayChecked = MutableStateFlow(false)
    val todayChecked: StateFlow<Boolean> = _todayChecked

    init {
        loadAttendanceData()
    }

    private fun loadAttendanceData() {
        viewModelScope.launch {
            attendancePrefs.getCheckedDates().collect { dates ->
                _checkedDates.value = dates
                updateConsecutiveDays(dates)
            }
        }
        viewModelScope.launch {
            attendancePrefs.getTodayChecked().collect { checked ->
                _todayChecked.value = checked
            }
        }
    }

    fun checkAttendance() {
        viewModelScope.launch {
            val today = LocalDate.now()
            if (!_checkedDates.value.contains(today)) {
                val updated = _checkedDates.value + today
                _checkedDates.value = updated
                _todayChecked.value = true
                updateConsecutiveDays(updated)

                attendancePrefs.saveCheckedDates(updated)
                attendancePrefs.saveTodayChecked(true)
            }
        }
    }

    private fun updateConsecutiveDays(dates: List<LocalDate>) {
        val sorted = dates.sortedDescending()
        var count = 0
        var dateCursor = LocalDate.now()
        for (date in sorted) {
            if (date == dateCursor) {
                count++
                dateCursor = dateCursor.minusDays(1)
            } else {
                break
            }
        }
        _consecutiveDays.value = count
    }
    fun resetTodayChecked() {
        viewModelScope.launch {
            _todayChecked.value = false
            attendancePrefs.saveTodayChecked(false)
        }
    }
}


