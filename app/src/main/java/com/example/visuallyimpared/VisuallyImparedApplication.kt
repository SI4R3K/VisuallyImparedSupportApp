package com.example.visuallyimpared

import android.app.Application
import com.example.visuallyimpared.data.database.AppDatabase
import com.example.visuallyimpared.data.repository.GtfsRepository
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

    override fun onCreate() {
        super.onCreate()
//        checkAndImportData()
    }

//    private fun checkAndImportData() {
//        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
//        val isImported = true
////        val isImported = prefs.getBoolean("gtfs_imported", false)
//
//        if (!isImported) {
//            applicationScope.launch {
//                try {
//                    GtfsDataImporter(database).importFromAssets(this@VisuallyImparedApplication)
//                    prefs.edit().putBoolean("gtfs_imported", true).apply()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
}
