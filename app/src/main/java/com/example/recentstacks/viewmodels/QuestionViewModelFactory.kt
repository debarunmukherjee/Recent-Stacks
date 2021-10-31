package com.example.recentstacks.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recentstacks.repository.QuestionRepository

class QuestionViewModelFactory(private val repository: QuestionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuestionViewModel(repository) as T
    }
}