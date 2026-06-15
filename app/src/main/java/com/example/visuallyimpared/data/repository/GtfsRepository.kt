package com.example.visuallyimpared.data.repository

import android.util.Log
import com.example.visuallyimpared.data.database.dao.DepartureDao
import com.example.visuallyimpared.data.database.dao.StopDao
import com.example.visuallyimpared.data.dto.RouteInfo
import com.example.visuallyimpared.data.dto.StopInfo
import java.time.LocalDate
import java.time.LocalTime

class GtfsRepository(
    private val stopDao: StopDao,
    private val departureDao: DepartureDao
) {

    suspend fun getStopInfo(
        stopCode: String,
        date: String,
        fromTime: String,
        toTime: String,
        filterLines: List<String> = emptyList()
    ): StopInfo? {
        val stop =
            stopDao.getStopByCode(stopCode)
                ?: return null

        Log.d("GtfsRepository", "Found stop: $stop")

        val departures =
            departureDao.getDeparturesForStop(
                stopId = stop.stopId,
                date = date,
                fromTime = fromTime,
                toTime = toTime
                )

        Log.d("GtfsRepository", "Found departures: $departures")

        var routes =
            departures
                .groupBy { it.routeName to it.headSign }
                .map { (key, items) ->

                    val (routeName, headSign) = key

                    RouteInfo(
                        routeName = routeName,
                        headSign = headSign,
                        departureTimes =
                            items
                                .take(2)
                                .map { it.departureTime }
                    )
                }

        if (filterLines.isNotEmpty()) {
            routes = routes.filter { route ->
                filterLines.any { filter ->
                    route.routeName.equals(filter, ignoreCase = true)
                }
            }
        }

        Log.d("GtfsRepository", "Filter lines: $filterLines")

        return StopInfo(
            stopName = stop.stopName,
            routes = routes
        )
    }
}