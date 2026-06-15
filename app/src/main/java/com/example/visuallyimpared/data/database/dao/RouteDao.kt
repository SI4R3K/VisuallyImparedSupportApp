package com.example.visuallyimpared.data.database.dao

import androidx.room.*
import com.example.visuallyimpared.data.database.entity.RouteEntity

@Dao
interface RouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<RouteEntity>)

    @Insert
    fun insert(route: RouteEntity)


    @Query("SELECT * FROM routes")
    suspend fun getAll(): List<RouteEntity>
}
