package com.example.visuallyimpared.viewModel

import android.net.Uri
import com.example.visuallyimpared.data.dto.StopInfo

data class ImageRecognitionUiState(
    val selectedImageUri: Uri? = null,
    val stopId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
