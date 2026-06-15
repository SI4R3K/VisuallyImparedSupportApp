package com.example.visuallyimpared.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trips",
    indices = [
        Index("trip_id"),
        Index("route_id"),
        Index("service_id"),
        Index("trip_headsign")
    ]
)
data class TripEntity(
    @PrimaryKey
    @ColumnInfo(name = "trip_id")
    val tripId: String,

    @ColumnInfo(name = "route_id")
    val routeId: String,

    @ColumnInfo(name = "service_id")
    val serviceId: String,

    @ColumnInfo(name = "trip_headsign")
    val tripHeadsign: String
)
