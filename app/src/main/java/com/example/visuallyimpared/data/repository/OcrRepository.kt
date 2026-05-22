package com.example.visuallyimpared.data.repository

import com.example.visuallyimpared.data.database.OcrDao
import com.example.visuallyimpared.data.database.OcrRecord
import kotlinx.coroutines.flow.Flow

class OcrRepository(private val ocrDao: OcrDao) {

    fun getAllRecords(): Flow<List<OcrRecord>> = ocrDao.getAll()

    suspend fun insertRecord(text: String) {
        val record = OcrRecord(
            text = text,
            timeStamp = System.currentTimeMillis()
        )
        ocrDao.insert(record)
    }

    suspend fun deleteRecord(id: Int) {
        ocrDao.deleteById(id)
    }
}
