package com.example.visuallyimpared.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.visuallyimpared.data.database.projection.DepartureProjection

@Dao
interface DepartureDao {

    @Query(
        """
    SELECT 
        routes.route_short_name AS routeName,
        stop_times.departure_time AS departureTime,
        trips.trip_headsign AS headSign
    FROM stop_times
    JOIN trips
        ON stop_times.trip_id = trips.trip_id
    JOIN routes
        ON trips.route_id = routes.route_id
    JOIN calendar_dates
        ON trips.service_id = calendar_dates.service_id
    WHERE stop_times.stop_id = :stopId
        AND calendar_dates.date = :date
        AND stop_times.departure_time > :fromTime
        AND stop_times.departure_time < :toTime
    ORDER BY stop_times.departure_time
    LIMIT 100
    """
    )
    suspend fun getDeparturesForStop(
        stopId: String,
        date: String,
        fromTime: String,
        toTime: String
    ): List<DepartureProjection>

}