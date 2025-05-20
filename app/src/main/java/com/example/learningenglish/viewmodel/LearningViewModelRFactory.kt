package com.example.learningenglish.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.repository.WordRepository

class LearningViewModelFactory(
    private val repository: LearningRepository,
    private val repositoryW: WordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LearningViewModel(repository, repositoryW) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
