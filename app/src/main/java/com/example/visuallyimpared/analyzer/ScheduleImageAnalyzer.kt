package com.example.visuallyimpared.analyzer

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException

class ScheduleImageAnalyzer(
    private val context: Context,
    private val onRecognized: (String) -> Unit
) {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Call this method from your Screen (e.g., when clicking the Recognize button)
     */
    fun analyze(uri: Uri) {
        try {
            // 1. Prepare the image from the URI
            val image = InputImage.fromFilePath(context, uri)

            // 2. Start the recognition process
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // 3. Pass the result back via the callback
                    onRecognized(visionText.text)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    onRecognized("Error: Could not recognize text")
                }
        } catch (e: IOException) {
            e.printStackTrace()
            onRecognized("Error: Could not load image file")
        }
    }
}