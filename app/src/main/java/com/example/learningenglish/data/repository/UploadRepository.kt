package com.example.learningenglish.data.repository

import android.content.Context
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
import retrofit2.Response

class LearningRepository {

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

    suspend fun getLibraryForUser(userId: String): List<UserLibraryContent> {
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

    suspend fun fetchVideoDetail(id: Int): VideoDetailResponse? {
        val response = RetrofitInstance.api.getVideoDetail(id)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun fetchWordDetail(word: String): WordDetailResponse {
        return RetrofitInstance.api.getWordDetail(word)
    }

    suspend fun uploadTextContent(data: OcrUploadRequest): Response<LearningResponse> {
        return RetrofitInstance.api.uploadTextOcr(data)
    }


    /*
    suspend fun getWordDetail(word: String): WordDetailResponse? {
        return try {
            api.getWordDetail(word)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addToVocab(word: String, uid: String) {
        try {
            val body = mapOf(
                "word" to word,
                "uid" to uid
            )
            api.addToVocab(body)
        } catch (_: Exception) { }
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

    suspend fun addToVocab(word: String, uid: String) {
        try {
            val body = mapOf(
                "word" to word,
                "uid" to uid
            )
            securedApi.addToVocab(body)
        } catch (_: Exception) {}
    }

    suspend fun fetchUserVocabPaged(uid: String, page: Int): List<WordDetailResponse> {
        return try {
            api.getUserVocabPaged(uid, page)
        } catch (e: Exception) {
            emptyList()  // 오류 시 빈 리스트 반환
        }
    }

}



