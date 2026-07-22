package com.example.visuallyimpared.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.analyzer.ScheduleImageAnalyzer
import com.example.visuallyimpared.utils.loadBitmapFromUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UploadScreenViewModel(
    private val analyzer: ScheduleImageAnalyzer,
): ViewModel() {

    private val _uiState = MutableStateFlow(UploadScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun setImage(uri: Uri?) {
        _uiState.update {
            it.copy(selectedImageUri = uri, errorMessage = null, stopId = null)
        }
    }

    fun reset() {
        _uiState.value = UploadScreenUiState()
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

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        stopId = stop.stopId
                    )
                }

                onRecognized(stop.stopId)
            } catch (e: NoSuchElementException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Nie znaleziono identyfikatora przystanku. Spróbuj wgrać wyraźniejsze zdjęcie."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Błąd rozpoznawania: ${e.localizedMessage ?: "Nieznany błąd"}"
                    )
                }
            }

        }
    }
}