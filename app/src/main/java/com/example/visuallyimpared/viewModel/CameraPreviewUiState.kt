package com.example.visuallyimpared.viewModel

import android.net.Uri

data class CameraPreviewUiState(
    val capturedImageUri: Uri? = null,
    val isCapturing: Boolean = false,
    val error: String? = null
)
