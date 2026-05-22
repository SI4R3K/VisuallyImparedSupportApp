package com.example.visuallyimpared.analyzer

import android.util.Log
import com.google.mlkit.vision.text.Text

class TextPostProcessor {
    private var blocks: List<Text.TextBlock> = emptyList()

    fun process(processedText: Text): Unit {
        blocks = processedText.textBlocks

        val sb = StringBuilder()
        for (block in blocks) {
            sb.append(block.text).append("\n")
        }
        Log.d("TextPostProcessor", "Processed text: $sb")
    }

    fun getBlocks(): List<Text.TextBlock> {
        return blocks
    }
}