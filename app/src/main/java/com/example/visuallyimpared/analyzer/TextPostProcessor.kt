package com.example.visuallyimpared.analyzer

import android.util.Log
import com.example.visuallyimpared.data.ocr.DayType
import com.example.visuallyimpared.data.ocr.HourDeparture
import com.example.visuallyimpared.data.ocr.OcrItem
import com.example.visuallyimpared.data.ocr.Timetable
import com.google.mlkit.vision.text.Text
import kotlin.math.abs

class TextPostProcessor {

    private val timetables = mutableListOf<Timetable>()
    private var items: List<OcrItem> = emptyList()
    private lateinit var processedText: Text

    fun process(processedText: Text): List<Timetable> {

        this.processedText = processedText

        timetables.clear()

        items = getItems()

        val sortedLines = processedText.textBlocks
            .flatMap { it.lines }
            .sortedWith(
                compareBy<Text.Line>(
                    { (it.boundingBox?.centerY() ?: 0) / 20 },
                    { it.boundingBox?.centerX() ?: 0 }
                )
            )

        detectTables(sortedLines)

        parseTables()

        for (table in timetables) {
            for (departure in table.departures) {
                Log.d("OCR_DEPARTURE", "${departure.hour}:${departure.minutes}")
            }
        }

        return timetables
    }

    /**
     * =========================================
     * TABLE DETECTION
     * =========================================
     */
    private fun detectTables(sortedLines: List<Text.Line>) {

        // STEP 1
        // detect table headers

        for (line in sortedLines) {

            val box = line.boundingBox ?: continue

            val text = line.text
                .uppercase()
                .replace(" ", "")

            Log.d(
                "OCR_LINE",
                "${line.text} (${box.left}, ${box.top})"
            )

            when {

                text.contains("ROBOCZE") -> {

                    timetables.add(
                        Timetable(
                            dayType = DayType.WORKDAYS,
                            startX = box.left,
                            startY = box.bottom
                        )
                    )
                }

                text.contains("SOBOT") -> {

                    timetables.add(
                        Timetable(
                            dayType = DayType.SATURDAY,
                            startX = box.left,
                            startY = box.bottom
                        )
                    )
                }

                text.contains("NIEDZIEL") -> {

                    timetables.add(
                        Timetable(
                            dayType = DayType.SUNDAY_HOLIDAY,
                            startX = box.left,
                            startY = box.bottom
                        )
                    )
                }
            }
        }

        // sort left -> right

        timetables.sortBy { it.startX }

        if (timetables.isEmpty()) {
            Log.d("OCR_TABLE", "No tables detected")
            return
        }

        // STEP 2
        // detect GODZ / MINUTY headers

        val hourHeaders = items.filter {

            val t = it.text.uppercase()

            t.contains("GODZ") ||
                    t.contains("CODZ") ||
                    t.contains("G0DZ") ||
                    t.contains("60DZ")
        }

        val minuteHeaders = items.filter {

            val t = it.text.uppercase()

            t.contains("MINUT")
        }

        // STEP 3
        // assign bounds to tables

        for (i in timetables.indices) {

            val table = timetables[i]

            val nextTableStartX =
                if (i < timetables.lastIndex)
                    timetables[i + 1].startX
                else
                    Int.MAX_VALUE

            // find GODZ close to this table only

            val godz = hourHeaders
                .filter {

                    it.x < nextTableStartX &&
                            abs(it.y - table.startY) < 300
                }
                .minByOrNull {
                    abs(it.x - table.startX)
                }

            // find MINUTY close to this table only

            val minuty = minuteHeaders
                .filter {

                    it.x < nextTableStartX &&
                            abs(it.y - table.startY) < 300
                }
                .minByOrNull {
                    abs(it.x - table.startX)
                }

            if (godz != null) {

                table.startX =
                    godz.x - godz.width / 2

                table.startY =
                    godz.y + godz.height / 2
            }

            if (minuty != null) {

                table.endX =
                    minuty.x + minuty.width / 2 + 20
            } else {

                // fallback if MINUTY not found

                table.endX =
                    if (i < timetables.lastIndex)
                        nextTableStartX - 20
                    else
                        9999
            }
        }

        // STEP 4
        // detect table bottom

        for (table in timetables) {

            val hourColumnWidth = 120

            val hourItems = items.filter {

                val number = it.text.toIntOrNull()

                number != null &&
                        number in 0..23 &&
                        it.x in table.startX..(table.startX + hourColumnWidth) &&
                        it.y > table.startY
            }

            val bottom =
                hourItems.maxOfOrNull {
                    it.y + it.height / 2
                } ?: table.startY

            table.endY = bottom + 20
        }

        // DEBUG

        for (table in timetables) {

            Log.d(
                "OCR_TABLE",
                """
                TABLE:
                TYPE=${table.dayType}
                START=(${table.startX}, ${table.startY})
                END=(${table.endX}, ${table.endY})
                """.trimIndent()
            )
        }
    }

    /**
     * =========================================
     * TABLE PARSING
     * =========================================
     */
    private fun parseTables() {

        for (table in timetables) {

            val tableItems = items.filter {

                it.x in table.startX..table.endX &&
                        it.y in table.startY..table.endY
            }

            val rows = groupRows(tableItems)

            for (row in rows) {

                val sortedRow = row.sortedBy { it.x }

                if (sortedRow.isEmpty())
                    continue

                // first value = hour

                val hour =
                    sortedRow.first()
                        .text
                        .toIntOrNull()

                if (hour == null || hour !in 0..23)
                    continue

                // remaining values = minutes

                val minutes = sortedRow
                    .drop(1)
                    .mapNotNull {

                        val number = it.text.toIntOrNull()

                        if (number != null && number in 0..59)
                            number
                        else
                            null
                    }

                if (minutes.isEmpty())
                    continue

                table.departures.add(
                    HourDeparture(
                        hour = hour,
                        minutes = minutes
                    )
                )
            }

            Log.d(
                "OCR_PARSE",
                "Parsed ${table.departures.size} rows for ${table.dayType}"
            )
        }
    }

    /**
     * =========================================
     * ROW GROUPING
     * =========================================
     */
    private fun groupRows(
        items: List<OcrItem>
    ): List<List<OcrItem>> {

        val sorted = items.sortedBy { it.y }

        val rows = mutableListOf<MutableList<OcrItem>>()

        val threshold = 25

        for (item in sorted) {

            val existingRow = rows.find {

                abs(it.first().y - item.y) < threshold
            }

            if (existingRow != null) {

                existingRow.add(item)

            } else {

                rows.add(
                    mutableListOf(item)
                )
            }
        }

        return rows
    }

    /**
     * =========================================
     * OCR ITEMS
     * =========================================
     */
    private fun getItems(): List<OcrItem> {

        val result = mutableListOf<OcrItem>()

        for (block in processedText.textBlocks) {

            for (line in block.lines) {

                for (element in line.elements) {

                    val box =
                        element.boundingBox ?: continue

                    result.add(
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

        return result
    }
}