package com.example.learningenglish.ui.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences


val Context.dataStore by preferencesDataStore(name = "user_prefs")


class UserPreferencesDataStore(private val context: Context) {
    companion object {
        val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_NICKNAME_KEY = stringPreferencesKey("user_nickname")
    }

    suspend fun saveUserId(userId: Int) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    fun getUserId(): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }
    }

    suspend fun saveUserNickname(nickname: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NICKNAME_KEY] = nickname
        }
    }

    // 닉네임 가져오기
    fun getUserNickname(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_NICKNAME_KEY]
        }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }
}

