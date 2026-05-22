package com.example.visuallyimpared.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.data.database.OcrRecord
import com.example.visuallyimpared.data.repository.OcrRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OcrViewModel(private val repository: OcrRepository) : ViewModel() {

    val allRecords: StateFlow<List<OcrRecord>> = repository.getAllRecords()
        .onEach { records ->
            Log.d("OcrViewModel", "Current record count: ${records.size}")
            // Uncomment the line below to print all text to Logcat every time it changes
            // records.forEach { Log.d("OcrViewModel", "Data: ${it.text}") }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun saveRecognizedText(text: String) {
        if (text.isBlank()) return
        
        viewModelScope.launch {
            repository.insertRecord(text)
        }
    }
    
    // You can add more functions here later, like getting the history
}
