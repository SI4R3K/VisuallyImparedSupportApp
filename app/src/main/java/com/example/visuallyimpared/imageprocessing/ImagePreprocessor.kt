package com.example.visuallyimpared.imageprocessing

import android.graphics.Bitmap

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
    fun process(bitmap: Bitmap): Bitmap {
        val gray =
            grayscaleProcessor.process(bitmap)

        return thresholdProcessor.process(gray)
    }
}