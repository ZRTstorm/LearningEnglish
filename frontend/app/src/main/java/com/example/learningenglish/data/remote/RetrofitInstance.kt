package com.example.learningenglish.data.remote

import android.content.Context
import com.example.learningenglish.data.api.AuthInterceptor
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// RetrofitInstance.kt
object RetrofitInstance {
    private const val BASE_URL = "http://54.252.44.80:8080/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://54.252.44.80:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}

object AuthenticatedRetrofitClient {
    fun create(context: Context, userPrefs: UserPreferencesDataStore): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context, userPrefs)) // 👈 토큰 붙이기
            .build()

        return Retrofit.Builder()
            .baseUrl("http://54.252.44.80:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}


