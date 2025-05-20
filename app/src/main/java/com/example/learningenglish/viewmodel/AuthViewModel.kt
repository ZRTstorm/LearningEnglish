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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AppViewModel(context: Context) : ViewModel() {
    private val userPrefs = UserPreferencesDataStore(context)
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        viewModelScope.launch {
            userPrefs.getUserId().collect { id ->
                _userId.value = id
            }
        }
    }
}

fun logout(context: Context) {
    FirebaseAuth.getInstance().signOut()
    CoroutineScope(Dispatchers.IO).launch {
        UserPreferencesDataStore(context).clearUserId()
    }
}

// ✅ 3. getIdToken 최신화 방식
suspend fun getFreshIdToken(): String? {
    val user = FirebaseAuth.getInstance().currentUser
    return user?.getIdToken(true)?.await()?.token
}


// ✅ 5. authenticateWithServer 함수 예시 (AuthViewModel)
class AuthViewModel(
    private val authManager: AuthManager,
    private val userPrefs: UserPreferencesDataStore
) : ViewModel() {

    fun authenticateWithServer() {
        viewModelScope.launch {
            val idToken = authManager.getIdToken()
            if (idToken != null) {
                try {
                    val response = RetrofitClient.authApiService.sendIdToken(TokenRequest(idToken))
                    if (response.isSuccessful) {
                        val userId = response.body()?.userId
                        Log.d("ServerAuth", "userId: $userId")

                        response.body()?.userId?.let { userId ->
                            userPrefs.saveUserId(userId)
                        }

                    } else {
                        Log.e("ServerAuth", "인증 실패: ${response.code()} ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.e("ServerAuth", "네트워크 오류: ${e.localizedMessage}")
                }
            } else {
                Log.e("ServerAuth", "ID 토큰이 null입니다.")
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


/*
class AuthViewModel(private val authManager: AuthManager) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    fun authenticateWithServer() {
        viewModelScope.launch {
            val idToken = authManager.getIdToken()

            if (idToken != null) {
                try {
                    val response = RetrofitClient.authApiService.sendIdToken(TokenRequest(idToken))
                    if (response.isSuccessful) {
                        _userId.value = response.body()?.userId
                        Log.d("ServerAuth", "사용자 ID: ${_userId.value}")
                    } else {
                        _authError.value = "인증 실패: ${response.code()} ${response.errorBody()?.string()}"
                        Log.e("ServerAuth", _authError.value ?: "Unknown error")
                    }
                } catch (e: Exception) {
                    _authError.value = "네트워크 오류: ${e.localizedMessage}"
                    Log.e("ServerAuth", _authError.value ?: "Unknown exception")
                }
            } else {
                _authError.value = "ID 토큰을 가져오지 못했습니다."
                Log.e("ServerAuth", _authError.value ?: "ID Token Error")
            }
        }
    }
}

 */
