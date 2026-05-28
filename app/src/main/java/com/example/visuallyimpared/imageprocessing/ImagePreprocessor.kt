package com.example.visuallyimpared.imageprocessing

import android.content.Context
import android.graphics.Bitmap
import com.example.visuallyimpared.debug.ImageDebugSaver

class ImagePreprocessor (
    private val grayscaleProcessor: GrayscaleProcessor =
        GrayscaleProcessor(),

    private val thresholdProcessor: ThresholdProcessor =
        ThresholdProcessor(),

//    private val perspectiveCorrector: PerspectiveCorrector =
//        PerspectiveCorrector(),
//
//    private val deskewProcessor: DeskewProcessor =
//        DeskewProcessor()
) {
    fun process(
        bitmap: Bitmap,
        context: Context
    ): Bitmap {

        ImageDebugSaver.saveBitmap(
            context = context,
            bitmap,
            "original.png"
        )

        val gray =
            grayscaleProcessor.process(bitmap)

        ImageDebugSaver.saveBitmap(
            context = context,
            gray,
            "grayscale.png"
        )

        val threshold = thresholdProcessor.process(gray)

        ImageDebugSaver.saveBitmap(
            context = context,
            threshold,
            "threshold.png"
        )

        return gray
    }
}