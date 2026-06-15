package com.example.visuallyimpared.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey
    @ColumnInfo(name = "route_id")
    val routeId: String,

    @ColumnInfo(name = "route_short_name")
    val routeName: String,
)
