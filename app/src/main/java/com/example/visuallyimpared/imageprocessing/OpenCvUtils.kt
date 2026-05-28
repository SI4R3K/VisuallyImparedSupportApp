package com.example.visuallyimpared.imageprocessing

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.Mat

object OpenCvUtils {

    fun bitmapToMat(bitmap: Bitmap): Mat {

        val mat = Mat()

        Utils.bitmapToMat(bitmap, mat)

        return mat
    }

    fun matToBitmap(mat: Mat): Bitmap {

        val bitmap = Bitmap.createBitmap(
            mat.cols(),
            mat.rows(),
            Bitmap.Config.ARGB_8888
        )

        Utils.matToBitmap(mat, bitmap)

        return bitmap
    }
}