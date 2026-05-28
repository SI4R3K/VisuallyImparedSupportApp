package com.example.visuallyimpared.debug

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.text.Text
import java.io.File
import java.io.FileOutputStream

object OcrDebugSaver {

    fun saveRecognizedText(
        context: Context,
        text: Text,
        filename: String
    ) {
        val dir = File(
            context.getExternalFilesDir(null),
            "debug"
        )

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, filename)

        val builder = StringBuilder()

        builder.appendLine("====== OCR DEBUG =====")
        builder.appendLine()

//        val sortedLines = text.textBlocks
//            .flatMap { it.lines }
//            .sortedWith(
//                compareBy<Text.Line>(
//                    { (it.boundingBox?.centerY() ?: 0) / 20 },
//                    { it.boundingBox?.centerX() ?: 0 }
//                )
//            )

        for (block in text.textBlocks) {

            builder.appendLine("Block: ${block.text}")
            builder.appendLine()

            for (line in block.lines) {

                val box = line.boundingBox
                builder.appendLine(
                    "Line: ${line.text}"
                )
                builder.appendLine(
                    "POSITION: x=${box?.left}, y=${box?.top}"
                )
                builder.appendLine(
                    "WIDTH=${box?.width()} HEIGHT=${box?.height()}"
                )
                builder.appendLine()

                for (element in line.elements) {

                    val elementBox =
                        element.boundingBox
                    builder.appendLine(
                        "    ELEMENT: ${element.text}"
                    )
                    builder.appendLine(
                        "    x=${elementBox?.left}, y=${elementBox?.top}"
                    )
                    builder.appendLine()
                }
                builder.appendLine(
                    "-------------------------"
                )
            }
            builder.appendLine()
        }

        FileOutputStream(file).use { out ->
            out.write(
                builder.toString()
                    .toByteArray()
            )
            out.flush()
        }

        Log.d(
            "OCR_DEBUG",
            "Saved OCR debug to: ${file.absolutePath}"
        )
    }
}