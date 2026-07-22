package com.example.visuallyimpared.viewModel

import android.net.Uri

data class UploadScreenUiState(
    val selectedImageUri: Uri? = null,
    val stopId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
