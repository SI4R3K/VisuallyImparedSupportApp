package com.example.visuallyimpared.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.visuallyimpared.data.database.AppDatabase
import com.example.visuallyimpared.data.database.GtfsDataImporter
import com.example.visuallyimpared.data.repository.GtfsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class GtfsIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: GtfsRepository
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = GtfsRepository(database.stopDao(), database.departureDao())
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun testImportAndRetrieveStopInfo() = runBlocking {
        // 1. Import data from assets
        val importer = GtfsDataImporter(database)
        importer.importFromAssets(context)

        // 2. Verify some data was imported
        val allStops = database.stopDao().getAllStops()
        assertTrue("Database should contain stops after import", allStops.isNotEmpty())
        
        val firstStop = allStops.first()
        val stopInfo = repository.getStopInfo(firstStop.stopCode)

        assertNotNull("Stop info should not be null for existing stop code", stopInfo)
        assertEquals(firstStop.stopName, stopInfo?.stopName)
    }

    @Test
    fun testFilterRoutes() = runBlocking {
        val importer = GtfsDataImporter(database)
        importer.importFromAssets(context)

        val stopCode = "0001"
        // Try to filter by a line that we expect to exist. 
        // We'll first get all lines to see what's available if we don't know.
        val fullInfo = repository.getStopInfo(stopCode)
        val availableLines = fullInfo?.routes?.map { it.routeName } ?: emptyList()
        
        if (availableLines.isNotEmpty()) {
            val filterLine = availableLines[0]
            val filteredInfo = repository.getStopInfo(stopCode, listOf(filterLine))
            
            assertNotNull(filteredInfo)
            assertTrue("Should only contain the filtered line", filteredInfo?.routes?.all { it.routeName == filterLine } ?: false)
            assertEquals(1, filteredInfo?.routes?.size)
        }
    }
}
