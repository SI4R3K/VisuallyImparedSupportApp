package com.example.visuallyimpared.analyzer

import android.util.Log
import com.example.visuallyimpared.data.ocr.DayType
import com.example.visuallyimpared.data.ocr.HourDeparture
import com.example.visuallyimpared.data.ocr.OcrItem
import com.example.visuallyimpared.data.ocr.Timetable
import com.google.mlkit.vision.text.Text

class TextPostProcessor {

    fun process(processedText: Text): List<Timetable> {

        val items = mutableListOf<OcrItem>()

        for (block in processedText.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val box = element.boundingBox ?: continue

                    items.add(
                        OcrItem(
                            text = element.text,
                            x = box.centerX(),
                            y = box.centerY(),
                            width = box.width(),
                            height = box.height()
                        )
                    )
                }
            }
        }

        val tables = getTables(items)
        tables.forEach { table ->
            Log.d("OCR", "TABLE IDENTIFIED: Day=${table.dayType}, Departures=${table.departures.size}")
            table.departures.forEach { dep ->
                Log.d("OCR", "  Hour ${dep.hour}: ${dep.minutes.joinToString(", ")}")
            }
        }
        return tables
    }

    private fun groupRows(items: List<OcrItem>): List<List<OcrItem>> {
        if (items.isEmpty()) return emptyList()
        val sorted = items.sortedBy { it.y }

        val rows = mutableListOf<MutableList<OcrItem>>()

        val avgHeight = if (items.isNotEmpty()) items.map { it.height }.average() else 20.0
        val threshold = avgHeight * 0.7

        for (item in sorted) {

            val row = rows.find {
                kotlin.math.abs(it.first().y - item.y) < threshold
            }

            if (row != null) {
                row.add(item)
            } else {
                rows.add(mutableListOf(item))
            }
        }

        return rows.map { row ->
            row.sortedBy { it.x }
        }
    }

    private fun getTables(items: List<OcrItem>): List<Timetable> {
        val result = mutableListOf<Timetable>()
        
        // 1. Find all "GODZ." markers
        val godzMarkers = items.filter { it.text.contains("GODZ", ignoreCase = true) }
        
        godzMarkers.forEach { godz ->
            // 2. Find the matching "MINUTY" header (same row, to the right)
            val minuty = items.find { 
                it.text.contains("MIN", ignoreCase = true) && 
                kotlin.math.abs(it.y - godz.y) < godz.height && 
                it.x > godz.x 
            } ?: return@forEach

            // 3. Identify DayType by looking above the headers
            val dayType = findDayTypeAbove(items, godz)

            // 4. Define the column boundaries
            val leftBound = godz.x - godz.width
            val midBound = (godz.x + minuty.x) / 2
            val rightBound = minuty.x + minuty.width * 5 // Buffer for minutes

            // 5. Find all items below the headers that belong to this table area
            val tableData = items.filter { 
                it.y > godz.y + godz.height && 
                it.x in (leftBound - 20)..rightBound 
            }.filter { it.y < godz.y + 1500 } // Safety limit for table height

            // 6. Group these items into rows to extract HourDepartures
            val rows = groupRows(tableData)
            val departures = mutableListOf<HourDeparture>()
            
            var maxY = godz.y + godz.height

            rows.forEach { row ->
                // Split row into hour (left) and minutes (right) based on midBound
                val hourItem = row.find { it.x < midBound }
                val minutesItems = row.filter { it.x >= midBound }

                val hourText = hourItem?.text?.filter { it.isDigit() } ?: ""
                val hour = hourText.toIntOrNull()
                
                if (hour != null) {
                    val minutes = mutableListOf<Int>()
                    minutesItems.forEach { mItem ->
                        val extracted = mItem.text.split(Regex("[^0-9]+"))
                            .filter { s -> s.isNotEmpty() }
                            .mapNotNull { s -> s.toIntOrNull() }
                        minutes.addAll(extracted)
                    }
                    departures.add(HourDeparture(hour, minutes))
                    maxY = maxOf(maxY, row.maxOf { it.y + it.height / 2 })
                }
            }

            if (departures.isNotEmpty()) {
                result.add(
                    Timetable(
                        dayType = dayType,
                        departures = departures,
                        startX = leftBound,
                        startY = godz.y - (godz.height * 3), // Estimate to include labels above
                        width = rightBound - leftBound,
                        height = maxY - (godz.y - godz.height * 3)
                    )
                )
            }
        }

        return result
    }

    private fun findDayTypeAbove(items: List<OcrItem>, godz: OcrItem): DayType? {
        // Look for text within a certain vertical distance above the header
        val labelsAbove = items.filter { 
            it.y < godz.y && 
            it.y > godz.y - (godz.height * 10) && 
            kotlin.math.abs(it.x - godz.x) < 600 
        }.sortedByDescending { it.y }

        val combinedText = labelsAbove.joinToString(" ") { it.text }.uppercase()
        
        return when {
            combinedText.contains("ROBOCZE") -> DayType.WORKDAYS
            combinedText.contains("SOBOTY") -> DayType.SATURDAY
            combinedText.contains("NIEDZIELE") || combinedText.contains("ŚWIĘTA") -> DayType.SUNDAY_HOLIDAY
            else -> null
        }
    }
}
