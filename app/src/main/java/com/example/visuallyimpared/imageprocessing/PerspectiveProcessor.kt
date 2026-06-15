package com.example.visuallyimpared.imageprocessing

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.visuallyimpared.debug.ImageDebugSaver
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.sqrt

class PerspectiveProcessor {

    /**
     * Attempts to find a document-like quadrilateral in the image and performs
     * a perspective transform to "flatten" it.
     */
    fun process(bitmap: Bitmap, context: Context): Bitmap {
        val src = OpenCvUtils.bitmapToMat(bitmap)

        // 1. Resize for more robust edge detection (standardizes noise and scale)
        val ratio = src.height().toDouble() / 500.0
        val height = 500
        val width = (src.width() / ratio).toInt()
        val resized = Mat()
        Imgproc.resize(src, resized, Size(width.toDouble(), height.toDouble()))

        // 2. Preprocessing for better edge detection
        val gray = Mat()
        Imgproc.cvtColor(resized, gray, Imgproc.COLOR_RGBA2GRAY)

        // Median blur is effective against "salt and pepper" noise
        val blurred = Mat()
        Imgproc.medianBlur(gray, blurred, 9)

        // 3. Edge detection
        val edges = Mat()
        Imgproc.Canny(blurred, edges, 50.0, 150.0)

        // 4. Dilation to bridge gaps in fragmented document borders
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
        val dilated = Mat()
        Imgproc.dilate(edges, dilated, kernel)

        // 5. Find contours
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            dilated,
            contours,
            Mat(),
            Imgproc.RETR_LIST,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        var biggestQuad: MatOfPoint2f? = null
        val imageArea = resized.width().toDouble() * resized.height()

        // Sort by area and only consider contours that take up a significant part of the screen
        val sortedContours = contours
            .filter { Imgproc.contourArea(it) > 0.1 * imageArea }
            .sortedByDescending { Imgproc.contourArea(it) }

        for (contour in sortedContours) {
            val contour2f = MatOfPoint2f(*contour.toArray())
            val perimeter = Imgproc.arcLength(contour2f, true)
            val approx = MatOfPoint2f()

            // Iteratively try to approximate the contour to a 4-point polygon
            for (epsIndex in 1..10) {
                val epsilon = (epsIndex * 0.01) * perimeter
                Imgproc.approxPolyDP(contour2f, approx, epsilon, true)

                if (approx.total() == 4L) {
                    biggestQuad = approx
                    break
                }
            }
            if (biggestQuad != null) break
        }

        if (biggestQuad == null) {
            Log.d("OCR_PERSPECTIVE", "No quad found. Returning original.")
            cleanup(src, resized, gray, blurred, edges, dilated)
            return bitmap
        }

        // 6. Scale points back to original image size
        val points = biggestQuad.toArray().map { Point(it.x * ratio, it.y * ratio) }.toTypedArray()
        val ordered = orderPoints(points)

        val topLeft = ordered[0]
        val topRight = ordered[1]
        val bottomRight = ordered[2]
        val bottomLeft = ordered[3]

        // 7. Calculate dimensions of the new image
        val targetWidth = maxOf(distance(bottomLeft, bottomRight), distance(topLeft, topRight))
        val targetHeight = maxOf(distance(topRight, bottomRight), distance(topLeft, bottomLeft))

        val srcPoints = MatOfPoint2f(topLeft, topRight, bottomRight, bottomLeft)
        val dstPoints = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(targetWidth - 1, 0.0),
            Point(targetWidth - 1, targetHeight - 1),
            Point(0.0, targetHeight - 1)
        )

        // 8. Warp Perspective
        val transformMatrix = Imgproc.getPerspectiveTransform(srcPoints, dstPoints)
        val result = Mat()
        Imgproc.warpPerspective(src, result, transformMatrix, Size(targetWidth, targetHeight))

        val resultBitmap = OpenCvUtils.matToBitmap(result)

        cleanup(src, resized, gray, blurred, edges, dilated, result, transformMatrix)
        return resultBitmap
    }

    private fun cleanup(vararg mats: Mat) {
        for (mat in mats) {
            mat.release()
        }
    }

    private fun distance(p1: Point, p2: Point): Double {
        return sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y))
    }

    private fun orderPoints(points: Array<Point>): Array<Point> {
        val ordered = Array(4) { Point() }
        val sums = points.map { it.x + it.y }
        val diffs = points.map { it.x - it.y }

        // TL has the smallest sum
        ordered[0] = points[sums.indexOf(sums.min())]
        // BR has the largest sum
        ordered[2] = points[sums.indexOf(sums.max())]
        // TR has the largest difference (x - y)
        ordered[1] = points[diffs.indexOf(diffs.max())]
        // BL has the smallest difference (x - y)
        ordered[3] = points[diffs.indexOf(diffs.min())]

        return ordered
    }
}
