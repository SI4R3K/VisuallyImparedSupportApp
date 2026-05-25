package com.example.visuallyimpared.data.ocr

/**
 * Represents a single table on the bus schedule (e.g., for one DayType).
 */
data class Timetable(
    val dayType: DayType? = null,
    val departures: List<HourDeparture> = emptyList(),
    val startX: Int = 0,
    val startY: Int = 0,
    val width: Int = 0,
    val height: Int = 0
)

data class HourDeparture(
    val hour: Int,
    val minutes: List<Int>
)

data class OcrItem(
    val text: String,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

enum class DayType {
    WORKDAYS,
    SATURDAY,
    SUNDAY_HOLIDAY
}
