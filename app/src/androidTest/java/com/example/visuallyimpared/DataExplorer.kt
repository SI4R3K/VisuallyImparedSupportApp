package com.example.visuallyimpared

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A utility "Test" to work with saved data without running the full app UI.
 * You can run this by clicking the 'Play' icon in the gutter.
 * Results will be printed to Logcat (filter by tag "DATA_EXPLORER").
 */
@RunWith(AndroidJUnit4::class)
class DataExplorer {

    @Test
    fun printSavedData() {
        runBlocking {
            val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as VisuallyImparedApp
            val repository = context.repository
            
            Log.d("DATA_EXPLORER", "--- FETCHING SAVED DATA ---")
            
            val records = repository.getAllRecords().first()
            
            if (records.isEmpty()) {
                Log.d("DATA_EXPLORER", "Database is empty.")
            } else {
                records.forEachIndexed { index, record ->
                    Log.d("DATA_EXPLORER", "[$index] ID: ${record.id} | Text: ${record.text} | Time: ${record.timeStamp}")
                    
                    // You can add your processing logic here, for example:
                    // val processed = myNewLogic(record.text)
                    // Log.d("DATA_EXPLORER", "   Processed: $processed")
                }
            }
            
            Log.d("DATA_EXPLORER", "--- END OF DATA ---")
        }
    }
}
