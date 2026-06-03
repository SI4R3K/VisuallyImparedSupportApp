package com.example.visuallyimpared.analyzer

import android.util.Log
import com.example.visuallyimpared.data.ocr.OcrItem
import com.google.mlkit.vision.text.Text

fun sortLines(processedText: Text): List<Text.Line> {

    // sorting items by x and y
    val sortedLines = processedText.textBlocks
        .flatMap { it.lines }
        .sortedWith(
            compareBy<Text.Line>(
                { (it.boundingBox?.centerY() ?: 0) / 20 },
                { it.boundingBox?.centerX() ?: 0 }
            )
        )

    // DEBUG
    sortedLines.forEach { line ->
        Log.d("OCR_LINE", "${line.text}")
    }

    return sortedLines
}