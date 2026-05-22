package com.example.visuallyimpared.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.visuallyimpared.data.database.OcrRecord

@Dao
interface OcrDao {

    @Insert
    suspend fun insert(record: OcrRecord)

    @Query("SELECT * FROM OcrRecord ORDER BY timestamp DESC")
    fun getAll(): Flow<List<OcrRecord>>

    @Query("DELETE FROM OcrRecord WHERE id = :id")
    suspend fun deleteById(id: Int)
}