package com.example.visuallyimpared.debug

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object ImageDebugSaver {

    fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        filename: String
    ): File {

        val dir = File(
            context.getExternalFilesDir(null),
            "debug"
        )

        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, filename)

        FileOutputStream(file).use { out ->

            bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )

            out.flush()
        }

        return file
    }
}