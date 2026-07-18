package com.example.visuallyimpared.viewModel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.data.repository.GtfsRepository
import com.example.visuallyimpared.tts.TextToSpeechService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DepartureViewModel(
    private val repository: GtfsRepository,
    private val ttsService: TextToSpeechService
): ViewModel() {
    private val _uiState = MutableStateFlow(DepartureUiState())
    val uiState = _uiState.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _currentLanguage = MutableStateFlow("pl")
    val currentLanguage: StateFlow<String> = _currentLanguage

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate

    init {
        // Initialize TTS when ViewModel is created
        ttsService.init()
        ttsService.onSpeechStart = { _isSpeaking.value = true }
        ttsService.onSpeechDone = { _isSpeaking.value = false }
        ttsService.onSpeechError = { _isSpeaking.value = false }
    }

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

    fun updateSpeechRate(rate: Float) {
        _speechRate.value = rate
        ttsService.setSpeechRate(rate)
    }

    fun speakStopInfo() {
        val stopInfo = uiState.value.stopInfo ?: return
        
        if (_isSpeaking.value) {
            stopSpeaking()
            return
        }

        val text = buildString {
            append("Przystanek: ${stopInfo.stopName}. ")
            if (stopInfo.routes.isEmpty()) {
                append("Brak odjazdów dla rozpoznanych linii.")
            } else {
                stopInfo.routes.forEach { route ->
                    append("Linia ${route.routeName}, kierunek ${route.headSign}. ")
                    append("Odjazdy: ${route.departureTimes.joinToString(", ")}. ")
                }
            }
        }
        speak(text, _speechRate.value)
    }

    fun speakRouteInfo(routeName: String, headSign: String, departures: List<String>) {
        val text = "Linia $routeName, kierunek $headSign. Odjazdy: ${departures.joinToString(", ")}"
        speak(text, _speechRate.value)
    }

    fun speak(text: String, rate: Float) {
        if (text.isBlank()) return

        setSpeechRate(rate)

        _isSpeaking.value = true
        ttsService.speak(text, currentLanguage.value)
    }

    fun speakWithLanguage(text: String, language: String) {
        if (text.isBlank()) return

        _currentLanguage.value = language
        _isSpeaking.value = true
        ttsService.speak(text, language)
    }

    fun stopSpeaking() {
        ttsService.stop()
        _isSpeaking.value = false
    }

    private fun setLanguage(language: String) {
        _currentLanguage.value = language
    }

    private fun setSpeechRate(rate: Float) {
        ttsService.setSpeechRate(rate)
    }

    fun ttsClear() {
        ttsService.shutdown()
    }

    override fun onCleared() {
        super.onCleared()
        ttsClear()
    }
}