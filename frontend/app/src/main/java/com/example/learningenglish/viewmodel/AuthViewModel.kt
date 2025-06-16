package com.example.learningenglish.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningenglish.data.remote.RetrofitClient
import com.example.learningenglish.data.remote.TokenRequest
import com.example.learningenglish.ui.auth.AuthManager
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AuthViewModel(
    private val authManager: AuthManager,
    private val userPrefs: UserPreferencesDataStore
) : ViewModel() {

    private val _userName = MutableStateFlow("사용자")
    val userName: StateFlow<String> = _userName

    init {
        loadUserDisplayName()
    }

    fun loadUserDisplayName() {
        viewModelScope.launch {
            val firebaseUser = authManager.getFirebaseUser()
            val nickname = userPrefs.getUserNickname().firstOrNull()
            val displayName = firebaseUser?.displayName

            _userName.value = when {
                !nickname.isNullOrBlank() -> nickname
                !displayName.isNullOrBlank() -> displayName
                else -> "사용자"
            }
        }
    }

    fun authenticateWithServer(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            val idToken = authManager.getIdToken()
            Log.d("AuthToken", "ID Token: $idToken")
            if (idToken != null) {
                try {
                    val response = RetrofitClient.authApiService.sendIdToken(TokenRequest(idToken))
                    Log.d("ServerAuth", "HTTP status: ${response.code()}")
                    Log.d("ServerAuth", "Response body: ${response.body()}")

                    if (response.isSuccessful) {
                        val userId: Int? = response.body()?.userId
                        Log.d("ServerAuth", "userId: $userId")

                        if (userId != null) {
                            userPrefs.saveUserId(userId)
                            onSuccess()
                        } else {
                            onFailure("서버에서 userId를 받지 못했습니다.")
                        }
                    } else {
                        onFailure("서버 응답 실패: ${response.code()}")
                    }
                } catch (e: Exception) {
                    onFailure("네트워크 오류: ${e.localizedMessage}")
                }
            } else {
                onFailure("ID 토큰이 null입니다.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
            authManager.signOutGoogle()
            userPrefs.clearUserId()
        }
    }

}

