package com.example.visuallyimpared.imageprocessing

import android.graphics.Bitmap
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ThresholdProcessor {
    fun process(bitmap: Bitmap): Bitmap {
        val mat = OpenCvUtils.bitmapToMat(bitmap)
        val thresholdMat = Mat()
        // Using adaptive thresholding for better results with varying lighting
        Imgproc.adaptiveThreshold(
            mat,
            thresholdMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            11,
            2.0
        )
        return OpenCvUtils.matToBitmap(thresholdMat)
    }
}