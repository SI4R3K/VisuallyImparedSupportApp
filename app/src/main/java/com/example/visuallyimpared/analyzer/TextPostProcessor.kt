package com.example.visuallyimpared.analyzer

import android.util.Log
import com.google.mlkit.vision.text.Text

/**
 * Handles cleaning and formatting of text recognized by ML Kit.
 * Working on this class via Unit Tests is much faster than 
 * redeploying the app and uploading photos.
 */
class TextPostProcessor {

    private var blocks: List<Text.TextBlock> = emptyList()

    fun process(processedText: Text): Unit {
        blocks = processedText.textBlocks

        val sb = StringBuilder()
        for (block in blocks) {
            sb.append(block.text).append("\n")
        }
        Log.d("TextPostProcessor"
        , "Recognized text: $sb")
    }
}
