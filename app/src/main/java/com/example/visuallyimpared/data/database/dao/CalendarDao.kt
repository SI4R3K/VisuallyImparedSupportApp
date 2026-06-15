package com.example.visuallyimpared.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.visuallyimpared.data.database.entity.CalendarEntity

@Dao
interface CalendarDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(calendars: List<CalendarEntity>)

    @Query("SELECT * FROM calendar_dates")
    suspend fun getAll(): List<CalendarEntity>
}