package com.example.visuallyimpared.data.ocr

data class Stop(
    val stopId: String,
    val lines: List<String> = emptyList()
)
