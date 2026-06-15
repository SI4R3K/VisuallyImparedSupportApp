package com.example.visuallyimpared.data.database.dao

import androidx.room.*
import com.example.visuallyimpared.data.database.entity.TripEntity

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trips: List<TripEntity>)

    @Insert
    fun insert(trip: TripEntity)

    @Query("SELECT * FROM trips")
    suspend fun getAll(): List<TripEntity>
}
