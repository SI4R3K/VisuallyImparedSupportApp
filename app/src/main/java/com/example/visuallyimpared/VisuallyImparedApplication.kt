package com.example.visuallyimpared

import android.app.Application
import com.example.visuallyimpared.analyzer.ScheduleImageAnalyzer
import com.example.visuallyimpared.data.database.AppDatabase
import com.example.visuallyimpared.data.repository.GtfsRepository
import com.example.visuallyimpared.tts.TextToSpeechService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class VisuallyImparedApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

    // Lazy initialization for the OCR analyzer
    val analyzer by lazy { ScheduleImageAnalyzer() }

    override fun onCreate() {
        super.onCreate()

        // Background pre-warming of heavy components
        applicationScope.launch(Dispatchers.Default) {
            // 1. Initialize Database & Repo (triggers lazy load)
            database
            gtfsRepository
            
            // 2. Start TTS initialization early
            ttsService
            
            // 3. Pre-warm ML Kit (triggers lazy loading of models)
            analyzer.warmUp()
        }
    }
}
