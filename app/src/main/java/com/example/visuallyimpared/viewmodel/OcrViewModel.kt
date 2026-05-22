package com.example.visuallyimpared.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.data.repository.OcrRepository
import kotlinx.coroutines.launch

class OcrViewModel(private val repository: OcrRepository) : ViewModel() {

    fun saveRecognizedText(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            repository.insertRecord(text)
        }
    }
    
    // You can add more functions here later, like getting the history
}
