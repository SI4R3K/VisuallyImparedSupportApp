package com.example.visuallyimpared.viewModel

import com.example.visuallyimpared.data.dto.StopInfo

data class DepartureUiState(
    val stopCode: String? = null,
    val stopInfo: StopInfo? = null,
    val currentTime: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)