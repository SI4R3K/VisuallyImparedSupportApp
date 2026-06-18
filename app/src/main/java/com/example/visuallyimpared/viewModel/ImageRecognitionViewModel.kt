package com.example.visuallyimpared.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.analyzer.ScheduleImageAnalyzer
import com.example.visuallyimpared.data.ocr.Stop
import com.example.visuallyimpared.data.repository.GtfsRepository
import com.example.visuallyimpared.utils.loadBitmapFromUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImageRecognitionViewModel(
    private val analyzer: ScheduleImageAnalyzer,
): ViewModel() {

    private val _uiState = MutableStateFlow(ImageRecognitionUiState())
    val uiState = _uiState.asStateFlow()

    fun setImage(uri: Uri?) {
        _uiState.update {
            it.copy(selectedImageUri = uri, errorMessage = null)
        }
    }

    fun reset() {
        _uiState.value = ImageRecognitionUiState()
    }

    fun recognizeImage(
        context: Context,
        onRecognized: (String?) -> Unit
    ) {
        val uri = _uiState.value.selectedImageUri ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val bitmap = loadBitmapFromUri(context, uri)

                if (bitmap == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Błąd: Nie można załadować obrazu"
                        )
                    }
                    return@launch
                }

                val stop = analyzer.analyze(bitmap, context)
                Log.d("OCR DEBUG", "recognized stop in ImagerRecognitionViewModel = $stop")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        stopId = stop.stopId
                    )
                }

                onRecognized(stop.stopId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Błąd: ${e.message}"
                    )
                }
            }

        }
    }
}