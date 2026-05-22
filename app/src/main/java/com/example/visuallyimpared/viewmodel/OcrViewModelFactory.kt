package com.example.visuallyimpared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.visuallyimpared.data.repository.OcrRepository

class OcrViewModelFactory(private val repository: OcrRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OcrViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OcrViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
