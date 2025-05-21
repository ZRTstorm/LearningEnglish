package com.example.learningenglish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.model.OcrUploadRequest
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.data.model.VideoDetailResponse
import com.example.learningenglish.data.model.WordDetailResponse
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.data.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortOption {
    TITLE, CATEGORY, UPLOADED_AT
}

enum class FilterOption {
    ALL, VIDEO_ONLY, IMAGE_ONLY
}

class LearningViewModel(
    private val repository: LearningRepository,
    private val repositoryW: WordRepository
) : ViewModel() {

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

    fun loadVideoDetail(id: Int) {
        viewModelScope.launch {
            val result = repository.fetchVideoDetail(id)
            _videoDetail.value = result
        }
    }

    fun loadLibraryForUser(userId: String) {
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
            SortOption.CATEGORY -> compareBy { it.category }
            SortOption.UPLOADED_AT -> compareBy { it.uploadedAt }
        }
    }

    private val _selectedWordInfo = MutableStateFlow<WordDetailResponse?>(null)
    val selectedWordInfo: StateFlow<WordDetailResponse?> = _selectedWordInfo

    fun loadWordDetail(word: String) {
        viewModelScope.launch {
            val response = repositoryW.getWordDetail(word)
            _selectedWordInfo.value = response
        }
    }

    fun addWordToUserVocab(word: String, uid: String) {
        viewModelScope.launch {
            repositoryW.addToVocab(word, uid) // POST or PUT, 서버 API에 따라
        }
    }

    private val _pagedUserVocab = MutableStateFlow<List<WordDetailResponse>>(emptyList())
    val pagedUserVocab: StateFlow<List<WordDetailResponse>> = _pagedUserVocab

    fun loadUserVocabPaged(uid: String, page: Int) {
        viewModelScope.launch {
            val result = repositoryW.fetchUserVocabPaged(uid, page)
            _pagedUserVocab.value = result
        }
    }

    fun submitOcrText(
        text: String,
        title: String,
        userId: String,
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





}

