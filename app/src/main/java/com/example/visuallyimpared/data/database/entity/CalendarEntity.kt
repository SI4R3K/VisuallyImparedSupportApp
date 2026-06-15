package com.example.visuallyimpared.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "calendar_dates",
    primaryKeys = ["service_id", "date"]
)
data class CalendarEntity(
    @ColumnInfo(name = "service_id")
    val serviceId: String,

    @ColumnInfo(name = "date")
    val date: String
)
