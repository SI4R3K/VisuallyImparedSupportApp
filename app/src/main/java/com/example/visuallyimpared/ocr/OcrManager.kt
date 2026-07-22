package com.example.visuallyimpared.ocr

import android.content.Context
import android.graphics.Bitmap
import com.example.visuallyimpared.debug.OcrDebugSaver
import com.google.mlkit.vision.text.Text
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OcrManager {
    private val recognizer = MlKitTextRecognizer()

    /**
     * Pre-warms the OCR engine.
     */
    fun warmUp() {
        recognizer.warmUp()
    }

    /**
     * Handles text recognition from a bitmap and returns the ML Kit Text object.
     * Converts the callback-based API to a suspend function for easier orchestration.
     */
    suspend fun recognizeText(
        bitmap: Bitmap,
        context: Context,
    ): Text = suspendCoroutine { continuation ->
        recognizer.recognize(
            bitmap = bitmap,
            onSuccess = { visionText ->
                continuation.resume(visionText)
            },
            onFailure = { exception ->
                continuation.resumeWithException(exception)
            }
        )
    }
}
