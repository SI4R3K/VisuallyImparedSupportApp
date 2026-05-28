package com.example.visuallyimpared.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class OcrManager {
    private val recognizer = MlKitTextRecognizer()
    /**
     * Handles text recognition from a bitmap and returns the ML Kit Text object.
     * Converts the callback-based API to a suspend function for easier orchestration.
     */
    suspend fun recognizeText(bitmap: Bitmap): Text = suspendCoroutine { continuation ->
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
