package com.example.visuallyimpared.analyzer

import android.util.Log
import com.google.mlkit.vision.text.Text

class TextPostProcessor {

    fun process(processedText: Text) {

        val items = mutableListOf<OcrItem>()

        for (block in processedText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val box = element.boundingBox ?: continue

                    items.add(
                        OcrItem(
                            text = element.text,
                            x = box.centerX(),
                            y = box.centerY(),
                            width = box.width(),
                            height = box.height()
                        )
                    )
                }
            }
        }

        Log.d("OCR", "ITEMS:")
        items.forEach {
            Log.d("OCR", "${it.text} (${it.x}, ${it.y})")
        }
    }
}

data class OcrItem(
    val text: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)
