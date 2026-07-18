package com.example.visuallyimpared

import android.app.Application
import com.example.visuallyimpared.data.database.AppDatabase
import com.example.visuallyimpared.data.repository.GtfsRepository
import com.example.visuallyimpared.tts.TextToSpeechService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VisuallyImparedApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Lazy initialization for the database and repository
    val database by lazy { AppDatabase.getDatabase(this) }
    val gtfsRepository by lazy {
        GtfsRepository(
            database.stopDao(),
            database.departureDao()
        )
    }

    // Lazy initialization for the TTS service
    val ttsService by lazy {
        TextToSpeechService(this).apply {
            init()
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

}
