package com.example.visuallyimpared.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "stop_times",
    primaryKeys = ["trip_id", "stop_id", "stop_sequence"],
    indices = [
        Index(value = ["trip_id"], name = "idx_stop_times_trip_id"),
        Index(value = ["stop_id"], name = "idx_stop_times_stop_id")
    ]
)
data class StopTimeEntity(
    @ColumnInfo(name = "trip_id")
    val tripId: String,

    @ColumnInfo(name = "departure_time")
    val departureTime: String,

    @ColumnInfo(name = "stop_id")
    val stopId: String,

    @ColumnInfo(name = "stop_sequence")
    val stopSequence: Int
)