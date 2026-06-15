package com.example.visuallyimpared.data.ocr

data class Stop(
    val stopId: String,
    val routes: List<String> = emptyList()
)
