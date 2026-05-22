package com.example.visuallyimpared

import android.app.Application
import androidx.room.Room
import com.example.visuallyimpared.data.database.AppDatabase
import com.example.visuallyimpared.data.repository.OcrRepository

class VisuallyImparedApp : Application() {

    private val database by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "visually_impared_db"
        ).build()
    }

    val repository by lazy {
        OcrRepository(database.ocrDao())
    }
}
