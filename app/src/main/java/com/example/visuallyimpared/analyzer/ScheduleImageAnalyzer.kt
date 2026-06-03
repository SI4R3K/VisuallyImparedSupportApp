package com.example.visuallyimpared.analyzer

import android.content.Context
import android.graphics.Bitmap
import com.example.visuallyimpared.data.ocr.Stop
import com.example.visuallyimpared.imageprocessing.ImagePreprocessor
import com.example.visuallyimpared.ocr.OcrManager

class ScheduleImageAnalyzer(
    private val imagePreprocessor: ImagePreprocessor = ImagePreprocessor(),
    private val ocrManager: OcrManager = OcrManager(),
    private val textPostProcessor: TextPostProcessor = TextPostProcessor()
) {
    /**
     * Orchestrates the full analysis flow:
     * 1. Preprocess image (OpenCV)
     * 2. Recognize text (ML Kit)
     * 3. Post-process text into Timetable data
     */
    suspend fun analyze(
        bitmap: Bitmap,
        context: Context
    ): Stop {

        // 1. Image Preprocessing (OpenCV)
        val processedBitmap = imagePreprocessor.process(bitmap, context)

        // 2. Text Recognition (ML Kit)
        val visionText = ocrManager.recognizeText(processedBitmap, context)

        // 3. Text Post-processing (Retrieving stop id and lines)
        return textPostProcessor.process(visionText)
    }
}
