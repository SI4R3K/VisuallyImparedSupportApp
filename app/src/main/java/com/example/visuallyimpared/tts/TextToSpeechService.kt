package com.example.visuallyimpared.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

class TextToSpeechService(
    private val context: Context
) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private var currentLanguage: String = "pl"
    private var pendingText: String? = null
    private var speechRate: Float = 1.0f

    // Call back for TTS event
    var onSpeechStart: (() -> Unit)? = null
    var onSpeechDone: (() -> Unit)? = null
    var onSpeechError: ((String) -> Unit)? = null

    fun init() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                setupTTS()

                // Speak any pending text
                pendingText?.let { text ->
                    speak(text, currentLanguage)
                    pendingText = null
                }
            } else {
                isInitialized = false
                onSpeechError?.invoke("TTS initialization failed")
            }
        }
    }

    private fun setupTTS() {
        textToSpeech?.let { tts ->
            // Set default language
            val locale = Locale(currentLanguage)
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                tts.setLanguage(Locale("pl", "PL"))
            }

            tts.setSpeechRate(speechRate)

            // Set up progress listener
            tts.setOnUtteranceProgressListener(object :
                UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    onSpeechStart?.invoke()
                }

                override fun onDone(utteranceId: String?) {
                    onSpeechDone?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    onSpeechError?.invoke("Speech error occurred")
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    val errorMessage = when (errorCode) {
                        TextToSpeech.ERROR_NETWORK -> "Network error"
                        TextToSpeech.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        TextToSpeech.ERROR_NOT_INSTALLED_YET -> "Language data not installed"
                        else -> "Internal error (code $errorCode)"
                    }
                    onSpeechError?.invoke("Speech error occurred: $errorMessage")
                }
            })
        }
    }

    fun speak(text: String, language: String) {
        if (text.isBlank()) return

        if (!isInitialized) {
            // Queue the text for later processing
            pendingText = text
            return
        }

        textToSpeech?.let { tts ->
            // Apply language change
            if (currentLanguage != language) {
                currentLanguage = language
                tts.setLanguage(Locale(language))
            }
            // Generate unique utterance ID for tracking
            val utteranceId = UUID.randomUUID().toString()

            // QUEUE_FLUSH stops current speech and starts new
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }

    fun setSpeechRate(rate: Float) {
        speechRate = rate
        textToSpeech?.setSpeechRate(rate)
    }

}