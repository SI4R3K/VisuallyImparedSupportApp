package com.example.visuallyimpared.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.visuallyimpared.data.database.entity.StopEntity

@Dao
interface StopDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stops: List<StopEntity>)

    @Insert
    fun insert(stop: StopEntity)

    @Query("""
        SELECT *
        FROM stops
        WHERE stop_code = :stopCode
    """)
    suspend fun getStopByCode(
        stopCode: String
    ): StopEntity?

    @Query("SELECT * FROM stops")
    suspend fun getAllStops(): List<StopEntity>
}
