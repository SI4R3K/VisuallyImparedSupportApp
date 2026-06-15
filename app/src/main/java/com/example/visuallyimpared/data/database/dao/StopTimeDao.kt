package com.example.visuallyimpared.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.visuallyimpared.data.database.entity.StopTimeEntity

@Dao
interface StopTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stopTimes: List<StopTimeEntity>)

    @Insert
    fun insert(stopTime: StopTimeEntity)

    @Query("SELECT * FROM stop_times")
    suspend fun getAll(): List<StopTimeEntity>
}
