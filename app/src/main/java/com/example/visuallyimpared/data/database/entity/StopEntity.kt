package com.example.visuallyimpared.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class StopEntity(
    @PrimaryKey
    @ColumnInfo(name = "stop_id")
    val stopId: String,

    @ColumnInfo(name = "stop_code")
    val stopCode: String,

    @ColumnInfo(name = "stop_name")
    val stopName: String,
    )
