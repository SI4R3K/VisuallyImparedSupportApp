package com.example.visuallyimpared.viewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.data.repository.GtfsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DepartureViewModel(
    private val repository: GtfsRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(DepartureUiState())
    val uiState = _uiState.asStateFlow()

    fun setStopCode(stopCode: String) {
        Log.d("OCR DEBUG", "setStopCode called with = $stopCode")
        _uiState.update {
            it.copy(stopCode = stopCode)
        }
    }
    fun reset() {
        _uiState.value = DepartureUiState()
    }

    fun generateStopInfo() {
        val stopCode = _uiState.value.stopCode ?: return

        val currentDate = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val now = LocalTime.now()
        val fromTime = now
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val toTime = now.plusHours(1)
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val stopInfo = repository.getStopInfo(
                    stopCode = stopCode,
                    date = currentDate,
                    fromTime = fromTime,
                    toTime = toTime
                )

                if (stopInfo == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Nie znaleziono przystanku: $stopCode"
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        stopInfo = stopInfo
                    )
                }
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