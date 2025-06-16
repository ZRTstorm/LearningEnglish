package com.example.learningenglish.data.api

import android.content.Context
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.data.util.getIdTokenOnce
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Context,
    private val userPrefs: UserPreferencesDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = runBlocking {
            getIdTokenOnce()
        }

        val newRequest = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(newRequest)
    }
}
