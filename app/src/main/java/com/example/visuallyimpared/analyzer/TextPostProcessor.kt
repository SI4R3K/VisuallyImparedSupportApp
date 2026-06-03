package com.example.visuallyimpared.analyzer

import android.util.Log
import com.example.visuallyimpared.data.ocr.OcrItem
import com.example.visuallyimpared.data.ocr.Stop
import com.google.mlkit.vision.text.Text
import kotlin.math.abs

class TextPostProcessor {
    fun process(processedText: Text): Stop {
        val processed = processedText

        // get sorted lines
        val sortedLines = sortLines(processed)

        // find stop id and return
        return findStop(sortedLines)
    }
}