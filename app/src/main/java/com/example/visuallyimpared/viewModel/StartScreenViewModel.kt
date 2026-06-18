package com.example.visuallyimpared.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class StartScreenViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(StartScreenUiState())
    val uiState: StateFlow<StartScreenUiState> = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri?) {
        _uiState.update {
            it.copy(
                selectedImageUri = uri,
                startState = false
            )
        }
    }

    fun onRedo() {
        _uiState.update {
            it.copy(
                startState = true,
                selectedImageUri = null
            )
        }
    }

    fun resetOnNextScreen() {
        _uiState.update {
            it.copy(
                startState = true
            )
        }
    }
}