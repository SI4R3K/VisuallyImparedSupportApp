package com.example.visuallyimpared.imageprocessing

import android.graphics.Bitmap
import org.opencv.imgproc.Imgproc

class GrayscaleProcessor {
    fun process(bitmap: Bitmap): Bitmap {
        val mat = OpenCvUtils.bitmapToMat(bitmap)
        val grayMat = org.opencv.core.Mat()
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY)
        return OpenCvUtils.matToBitmap(grayMat)
    }
}