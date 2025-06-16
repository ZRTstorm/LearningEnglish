package com.example.learningenglish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.ui.auth.AttendancePreferencesDataStore

class LearningViewModelFactory(
    private val repository: LearningRepository,
    private val repositoryW: WordRepository,
    private val attendancePrefs: AttendancePreferencesDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearningViewModel(repository, repositoryW, attendancePrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
