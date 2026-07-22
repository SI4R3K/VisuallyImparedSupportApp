package com.example.visuallyimpared.imageprocessing

import android.content.Context
import android.graphics.Bitmap
import com.example.visuallyimpared.debug.ImageDebugSaver

class ImagePreprocessor (
    private val grayscaleProcessor: GrayscaleProcessor =
        GrayscaleProcessor(),
) {
    fun process(
        bitmap: Bitmap,
        context: Context
    ): Bitmap {
//        ImageDebugSaver.saveBitmap(context, bitmap, "1_original.png")

        val gray = grayscaleProcessor.process(bitmap)
//        ImageDebugSaver.saveBitmap(context, gray, "2_grayscale.png")

        return gray
    }
}