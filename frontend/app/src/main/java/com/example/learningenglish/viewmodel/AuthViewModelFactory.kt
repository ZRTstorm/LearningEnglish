package com.example.learningenglish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learningenglish.ui.auth.AuthManager
import com.example.learningenglish.ui.auth.UserPreferencesDataStore

class AuthViewModelFactory(
    private val authManager: AuthManager,
    private val userPrefs: UserPreferencesDataStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authManager, userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
