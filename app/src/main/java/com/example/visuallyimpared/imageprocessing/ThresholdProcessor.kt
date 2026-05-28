package com.example.visuallyimpared.imageprocessing

import android.graphics.Bitmap
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

class ThresholdProcessor {
    fun process(bitmap: Bitmap): Bitmap {
        val gray = OpenCvUtils.bitmapToGrayMat(bitmap)

        // contrast
        val contrastMat = Mat()
        Imgproc.equalizeHist(gray, contrastMat)

        // blurred
        val blurredMat = Mat()
        Imgproc.GaussianBlur(
            contrastMat,
            blurredMat,
            org.opencv.core.Size(5.0, 5.0),
            0.0
        )

        // threshold
        val thresholdMat = Mat()

        // Using adaptive thresholding for better results with varying lighting
        Imgproc.adaptiveThreshold(
            blurredMat,
            thresholdMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            21,
            12.0
        )

        // cleaning small holes
        val kernel = Imgproc.getStructuringElement(
            Imgproc.MORPH_RECT,
            org.opencv.core.Size(2.0, 2.0)
        )

        Imgproc.morphologyEx(
            thresholdMat,
            thresholdMat,
            Imgproc.MORPH_CLOSE,
            kernel
        )

        return OpenCvUtils.matToBitmap(thresholdMat)
    }
}