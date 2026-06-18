package com.example.visuallyimpared.analyzer

import com.example.visuallyimpared.data.ocr.Stop
import com.google.mlkit.vision.text.Text
import android.graphics.Rect
import android.util.Log
import com.example.visuallyimpared.data.ocr.LineCandidate


fun findStop(
    sortedLines: List<Text.Line>
): Stop {

    val stopId = findStopId(sortedLines)
    val lines = findLines(sortedLines)

    return Stop(
        stopId = stopId,
//        routes = lines
    )
}


private fun findStopId(sortedLines: List<Text.Line>): String {

    val regex = """\((\d{4})\)""".toRegex()

    for (line in sortedLines) {

        val text = normalize(line.text)

        val match = regex.find(text)

        if (match != null) {
            return match.groupValues[1]
        }
    }

    throw NoSuchElementException("Stop ID not found")
}

private fun findLines(
    sortedLines: List<Text.Line>
): List<String> {

    val regex = """^(?:\d{1,2}[A-C]?|N\d{1,2}[A-C]?)$""".toRegex()

    val medianHeight = computeMedianHeight(sortedLines)

    val candidates = sortedLines.map { line ->

        val score = scoreLineCandidate(
            text = line.text,
            box = line.boundingBox,
            medianHeight = medianHeight,
            regex = regex
        )

        LineCandidate(
            value = normalize(line.text),
            score = score
        )
    }

    val topScores = candidates
                    .sortedByDescending { it.score }
                    .take(5)
                    .map { it.score}

    return candidates
        .sortedByDescending { it.score }
        .take(3)
        .map {it.value}
        .distinct()
}

private fun scoreLineCandidate(
    text: String,
    box: Rect?,
    medianHeight: Float,
    regex: Regex
): Double {

    val t = normalize(text)

    // ignore obvious noise
    if (t.length > 6) return 0.0
    if (t.contains("stop") || t.contains("przystanek")) return 0.0
    if (t.contains(":")) return 0.0 // godziny

    var base = 0.0

    // pattern match (strong signal)
    if (regex.matches(t)) base += 10

    // digits presence
    if (t.any { it.isDigit() }) base += 3

    // short text (lines are short)
    if (t.length <= 4) base += 2

    // SIZE FEATURE (very important)
    val height = box?.height()?.toFloat() ?: 0f

    val sizeMultiplier = if (medianHeight > 0f) {
        val ratio = height / medianHeight

        when {
            ratio > 1.8f -> 3.0
            ratio > 1.3f -> 2.0
            ratio > 1.0f -> 1.3
            ratio > 0.7f -> 0.6
            else -> 0.2   // mały tekst prawie ignorujemy
        }
    } else 1.0

    return base * sizeMultiplier
}

private fun computeMedianHeight(lines: List<Text.Line>): Float {

    val heights = lines
        .mapNotNull { it.boundingBox?.height()?.toFloat() }
        .sorted()

    if (heights.isEmpty()) return 0f

    return heights[heights.size / 2]
}

private fun normalize(text: String): String {
    return text
        .lowercase()
        .replace(" ", "")
        .replace("o", "0")
        .replace("i", "1")
        .replace("l", "1")
        .replace("s", "5")
        .replace("b", "8")
        .replace("z", "2")
        .replace("g", "6")
}